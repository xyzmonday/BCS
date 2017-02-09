package com.richfit.common_lib.exception;

/**
 * 异常基类
 * @version monday 2016-03
 */
public class Exception extends RuntimeException {

    public Exception() {
    }

    public Exception(String message) {
        super(message);
    }

    public Exception(String message, Throwable throwable) {
        super(message, throwable);
    }

    public Exception(Throwable throwable) {
        super(throwable);
    }

}
