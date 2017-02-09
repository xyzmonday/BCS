package com.richfit.data.cache.netcache.strategy;

import com.orhanobut.logger.Logger;
import com.richfit.data.cache.RxCache;
import com.richfit.data.cache.core.CacheTarget;
import com.richfit.data.cache.netcache.ResultData;
import com.richfit.data.cache.netcache.ResultFrom;

import io.reactivex.Flowable;


/**
 * 缓存策略。这里配合使用compose，通过不同的策略获取数据，如果我们的需求是：
 * 如果 (存在缓存) {
 * 读取缓存并显示
 * }
 * 请求网络
 * 写入缓存
 * 显示网络数据
 * 那么需要将firstOrError改成switchIfEmpty，并且拦截掉远程数据的onError回调；
 * 如果我们需要的是：
 * 如果 (存在缓存 且 缓存未过期) {
 * 读取缓存并显示
 * 返回
 * }
 * 请求网络
 * 更新缓存
 * 显示最新数据
 * 那么使用现在的缓存框架即可。
 * TODO:将上述两种方案结合
 *
 * @version monday 2016-07
 */
public enum CacheStrategy {
    /**
     * 仅缓存
     */
    OnlyCache {
        @Override
        public <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source) {
            return loadCache(key);
        }
    },
    /**
     * 仅网络
     */
    OnlyRemote {
        @Override
        public <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source) {
            return loadRemote(key, source);
        }
    },

    /**
     * 优先服务器。RxJava2.0的 filter+first等价于RxJava1.0的first(Fun1)。
     * RxJava1.0 的first->RxJava2.0 firstElement。
     */
    FirstRemote {
        @Override
        public <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source) {
            Flowable<ResultData<T>> cache = loadCache(key);
            Flowable<ResultData<T>> remote = loadRemote(key, source);
            return Flowable.concat(remote, cache)
                    .onBackpressureBuffer()
                    .filter(result -> result != null && result.data != null)
                    .firstOrError()
                    .toFlowable();
        }
    },

    /**
     * 优先缓存
     */
    FirstCache {
        @Override
        public <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source) {
            Flowable<ResultData<T>> cache = loadCache(key);
            Flowable<ResultData<T>> remote = loadRemote(key, source);
            return Flowable.concat(cache, remote)
                    .onBackpressureBuffer()
                    .filter(result -> result != null && result.data != null)
                    .firstOrError()// 最多发射一个数据，如果没有数据，则走 onError
                    .toFlowable();
//                    .switchIfEmpty(s -> s.onError(new NoSuchElementException()));//如果
        }
    },
    /**
     * 先缓存，后网络。注意这里先通从缓存得到数据，不管存在与否都会请求远程数据
     */
    CacheAndRemote {
        @Override
        public <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source) {
            Flowable<ResultData<T>> cache = loadCache(key);
            Flowable<ResultData<T>> remote = loadRemote(key, source);
            return Flowable.concat(cache, remote)
                    .onBackpressureBuffer()
                    .filter(result -> result.data != null);
        }
    };


    <T> Flowable<ResultData<T>> loadCache(final String key) {
        return RxCache.manager().load(key)
                .map(value -> new ResultData<>(ResultFrom.Cache, key, (T) value))
                .onErrorResumeNext(throwable -> {
                    return Flowable.empty();//拦截掉缓存 value == null的情况
                });
    }

    /**
     * 保存远程数据到缓存，注意这里我们没有拦击掉onError的情况，因为我们需要将错误信息返回给用户。
     *
     * @param key
     * @param source
     * @param <T>
     * @return
     */
    <T> Flowable<ResultData<T>> loadRemote(final String key, Flowable<T> source) {
        return source.map(value -> {
            RxCache.manager().save(key, value).subscribe(status -> Logger.d("保存远程数据" + (status ? "成功" : "失败")));
            return new ResultData<>(ResultFrom.Remote, key, value);
        });
    }

    <T> Flowable<ResultData<T>> loadRemote(final String key, Flowable<T> source, final CacheTarget target) {
        return source.map(value -> {
            RxCache.manager().save(key, value, target);
            return new ResultData<>(ResultFrom.Remote, key, value);
        });
    }

    public abstract <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source);

}
