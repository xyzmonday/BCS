/*
 * Copyright (C) 2016 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.richfit.data.cache.netcache.retrofit;


import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.data.cache.RxCache;
import com.richfit.data.cache.netcache.strategy.CacheStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

final class RxCallAdapter implements CallAdapter<Object> {
    private final Type responseType;
    private final Scheduler scheduler;
    private final boolean isResult;
    private final boolean isBody;
    private final boolean isFlowable;
    private final boolean isSingle;
    private final boolean isMaybe;
    private final boolean isCompletable;
    private final boolean isCache;
    private final Annotation[] annotations;

    RxCallAdapter(Type responseType, Scheduler scheduler, boolean isResult, boolean isBody,
                  boolean isFlowable, boolean isSingle, boolean isMaybe, boolean isCompletable,
                  boolean isCache, Annotation[] annotations) {
        this.responseType = responseType;
        this.scheduler = scheduler;
        this.isResult = isResult;
        this.isBody = isBody;
        this.isFlowable = isFlowable;
        this.isSingle = isSingle;
        this.isMaybe = isMaybe;
        this.isCompletable = isCompletable;
        this.isCache = isCache;
        this.annotations = annotations;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public <R> Object adapt(final Call<R> call) {
        Observable<Response<R>> responseObservable = new CallObservable<>(call);
        Observable<?> observable;

        if (isResult) {
            observable = new ResultObservable<>(responseObservable);
        } else if (isBody) {
            observable = new BodyObservable<>(responseObservable);
        } else {
            observable = responseObservable;
        }

        //处理订阅线程
        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }

        if (isFlowable) {
            if (isCache) {
                CacheInfo info = getCacheInfo(call,annotations);
                if (info != null) {
                    return observable.toFlowable(BackpressureStrategy.LATEST)
                            .compose(new RxCache.CacheTransformer(info.getKey(), info.getStrategy()));
                } else {
                    return observable.toFlowable(BackpressureStrategy.LATEST);
                }
            } else {
                return observable.toFlowable(BackpressureStrategy.LATEST);
            }
        }
        if (isSingle) {
            return observable.singleOrError();
        }
        if (isMaybe) {
            return observable.singleElement();
        }
        if (isCompletable) {
            return observable.ignoreElements();
        }
        //如理Observable
        if (isCache) {
            CacheInfo info = getCacheInfo(call,annotations);
            if (info != null) {
                return observable.toFlowable(BackpressureStrategy.LATEST)
                        .compose(new RxCache.CacheTransformer(info.getKey(), info.getStrategy()))
                        .toObservable();
            } else {
                return observable;
            }
        } else {
            return observable;
        }
    }

    private <R> CacheInfo getCacheInfo(final Call<R> call,Annotation[]annotation) {
        if (annotation == null) {
            return null;
        }
        //获取开启缓存的注解
        CacheInfo info = CacheInfo.get(annotations);
        if (info != null && info.isEnable()) {
            // 处理缓存
            if (CommonUtil.isEmpty(info.getKey())) {
                // 生成Key
                String key = CommonUtil.getHash(call.request());
                info.setKey(ByteString.of(key.getBytes()).md5().hex());
            }
            if (info.getStrategy() == null) {
                info.setStrategy(CacheStrategy.OnlyRemote);
            }
        }
        return info;
    }
}
