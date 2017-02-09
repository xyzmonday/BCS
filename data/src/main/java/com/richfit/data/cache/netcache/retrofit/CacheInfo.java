package com.richfit.data.cache.netcache.retrofit;


import com.richfit.data.cache.CACHE;
import com.richfit.data.cache.netcache.strategy.CacheStrategy;

import java.lang.annotation.Annotation;

/**
 * 缓存信息
 * @version monday 2016-07
 */
public class CacheInfo {

    public static CacheInfo get(Annotation[] annotations) {
        CacheInfo info = new CacheInfo();

        for (Annotation annotation : annotations) {
            if (annotation instanceof CACHE) {
                CACHE cache = (CACHE) annotation;
                info.key = cache.value();
                info.enable = cache.enable();
                info.strategy = cache.strategy();
                break;
            }
        }

        return info;
    }


    private String key;
    private boolean enable;
    private CacheStrategy strategy;

    public CacheInfo() {
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public CacheStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(CacheStrategy strategy) {
        this.strategy = strategy;
    }
}
