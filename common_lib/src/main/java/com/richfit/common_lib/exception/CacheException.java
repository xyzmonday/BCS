package com.richfit.common_lib.exception;

/**
 * 缓存处理错误
 * @version monday2016-03
 */
public class CacheException extends Exception {

    public CacheException() {
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CacheException(Throwable throwable) {
        super(throwable);
    }

}
