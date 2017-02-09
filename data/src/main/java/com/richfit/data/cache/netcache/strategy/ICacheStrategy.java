package com.richfit.data.cache.netcache.strategy;


import com.richfit.data.cache.netcache.ResultData;

import io.reactivex.Flowable;

/**
 * @version monday 2016-07
 */
public interface ICacheStrategy {

    <T> Flowable<ResultData<T>> execute(String key, Flowable<T> source);

}
