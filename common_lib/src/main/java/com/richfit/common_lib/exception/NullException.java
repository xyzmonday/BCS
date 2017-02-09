package com.richfit.common_lib.exception;



/**
 * 空数据错误
 * @version monday 2016-06
 */
public class NullException extends Exception {

    public NullException() {
    }

    public NullException(String message) {
        super(message);
    }

    public NullException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NullException(Throwable throwable) {
        super(throwable);
    }

}
