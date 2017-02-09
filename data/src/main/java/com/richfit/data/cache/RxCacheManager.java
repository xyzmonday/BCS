package com.richfit.data.cache;


import com.orhanobut.logger.Logger;
import com.richfit.common_lib.exception.NullException;
import com.richfit.data.cache.core.CacheCore;
import com.richfit.data.cache.core.CacheTarget;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.exceptions.Exceptions;

/**
 * RxJava模式缓存管理。实际上加载缓存的任务由CacheCore完成，
 * 而RxCacheManager的主要功能是将CahceCore返回的数据流T转换为
 * Flowable<T>.
 *
 * @version monday 2016-04
 */
public class RxCacheManager {

    private CacheCore mCacheCore;
    private int mDefaultExpires;

    private static abstract class SimpleSubscriber<T> implements FlowableOnSubscribe<T> {
        @Override
        public void subscribe(FlowableEmitter<T> emitter) throws Exception {
            try {
                T data = execute();
                if (data == null) {
                    emitter.onError(new NullException("未获取到缓存"));
                }
                emitter.onNext(data);
                emitter.onComplete();
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                emitter.onError(e);
                return;
            }
        }

        abstract T execute() throws Throwable;
    }


    /**
     * 构造函数
     *
     * @param cacheCore
     * @param defaultExpires 默认有效期（毫秒）
     */
    public RxCacheManager(CacheCore cacheCore, int defaultExpires) {
        this.mCacheCore = cacheCore;
        this.mDefaultExpires = defaultExpires;
    }

    /**
     * 读取缓存。注意这里mCacheCore.load必须在FlowableOnSubscribe里面，也就是说
     * 必须等待订阅者回到subscribe方法后才执行mCacheCore.load方法，这样它才能在子线程执行。
     */
    public <T> Flowable<T> load(final String key) {
        return Flowable.create(new SimpleSubscriber<T>() {
            @Override
            T execute() throws Throwable {
                T t = mCacheCore.load(key);
                Logger.d("读取到的缓存信息 = " + t);
                return t;
            }
        }, BackpressureStrategy.BUFFER);

    }

    /**
     * 保存
     */
    public <T> Flowable<Boolean> save(String key, T value) {
        return save(key, value, mDefaultExpires, CacheTarget.MemoryAndDisk);
    }

    public <T> Flowable<Boolean> save(String key, T value, CacheTarget target) {
        return save(key, value, mDefaultExpires, target);
    }

    /**
     * 保存
     *
     * @param expires 有效期（单位：秒）
     */
    public <T> Flowable<Boolean> save(final String key, final T value, final int expires, final CacheTarget target) {
        return Flowable.create(new SimpleSubscriber<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return mCacheCore.save(key, value, expires, target);
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 是否包含
     *
     * @param key
     * @return
     */
    public Flowable<Boolean> containsKey(final String key) {
        return Flowable.create(new SimpleSubscriber<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return mCacheCore.containsKey(key);
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 删除缓存
     *
     * @param key // TODO return Boolean?
     */
    public Flowable<Boolean> remove(final String key) {
        return Flowable.create(new SimpleSubscriber<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                mCacheCore.remove(key);
                return true;
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 清空缓存
     */
    public Flowable<Boolean> clear() {
        return Flowable.create(new SimpleSubscriber<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                mCacheCore.clear();
                return true;
            }
        }, BackpressureStrategy.BUFFER);
    }
}
