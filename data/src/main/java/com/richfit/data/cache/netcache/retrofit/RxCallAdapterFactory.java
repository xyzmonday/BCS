package com.richfit.data.cache.netcache.retrofit;


import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import com.richfit.data.cache.netcache.ResultData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * 支持缓存的RxJavaCallAdapterFactory。可以参看RxJava2CallAdapterFactory
 * 的源码，这里我们需要拿到自己定义的注解。
 *
 * @version monday 2016-07
 */
public class RxCallAdapterFactory extends CallAdapter.Factory {
    private final Scheduler scheduler;

    /**
     * Returns an instance which creates synchronous observables that do not operate on any scheduler
     * by default.
     */
    public static RxCallAdapterFactory create() {
        return new RxCallAdapterFactory(null);
    }

    /**
     * Returns an instance which creates synchronous observables that
     * {@linkplain Observable#subscribeOn(Scheduler) subscribe on} {@code scheduler} by default.
     */
    public static RxCallAdapterFactory createWithScheduler(Scheduler scheduler) {
        if (scheduler == null) throw new NullPointerException("scheduler == null");
        return new RxCallAdapterFactory(scheduler);
    }

    private RxCallAdapterFactory(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        //获取返回的数据类型可能是Observable,Flowable,Completable,Single,Maybe等
        Class<?> rawType = getRawType(returnType);

        //处理Completable
        if (rawType == Completable.class) {
            return new RxCallAdapter(Void.class, scheduler, false, true, false, false, false, true,
                    false, annotations);
        }

        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;

        //如果不是正常的RxJava操作符，那么返回为空。表示不能处理
        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }

        //处理响应返回的数据类型
        boolean isResult = false;
        boolean isBody = false;

        //自己添加的响应类型
        boolean isCache = false;

        Type responseType;
        //如果返回的类型没有参数化
        if (!(returnType instanceof ParameterizedType)) {
            String name = isFlowable ? "Flowable" : isSingle ? "Single" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }

        //获取参数化类型
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);

        //如果返回的Response类型，该类是Retrofit自带的
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized"
                        + " as Response<Foo> or Response<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);

            //如果返回的Retrofit的是Result数据类型
        } else if (rawObservableType == Result.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Result must be parameterized"
                        + " as Result<Foo> or Result<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isResult = true;

            //如果返回的是我们自己定义的缓存数据类型
        } else if (rawObservableType == ResultData.class) {
            responseType = observableType;
            isBody = true;
            isCache = true;
            CacheInfo info = CacheInfo.get(annotations);
            //拿到缓存注解
            if(info == null) {
                responseType = observableType;
                isCache = false;
            }
        } else {
            responseType = observableType;
            isBody = true;
        }

        return new RxCallAdapter(responseType, scheduler, isResult, isBody, isFlowable,
                isSingle, isMaybe, false, isCache, annotations);
    }
}
