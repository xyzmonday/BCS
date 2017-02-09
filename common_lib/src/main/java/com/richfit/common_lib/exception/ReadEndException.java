package com.richfit.common_lib.exception;

import java.lang.*;

/**
 * 已读取到末尾的异常
 * @version monday 2016-04
 */
public class ReadEndException extends java.lang.Exception {

    public ReadEndException() {
    }

    public ReadEndException(String message) {
        super(message);
    }

    public ReadEndException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ReadEndException(Throwable throwable) {
        super(throwable);
    }

}
