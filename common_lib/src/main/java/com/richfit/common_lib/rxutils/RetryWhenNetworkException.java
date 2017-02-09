package com.richfit.common_lib.rxutils;

import org.reactivestreams.Publisher;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class RetryWhenNetworkException implements Function<Flowable<? extends Throwable>, Publisher<?>> {
    private int count = 3;//retry count
    private long delay = 3000;//delay time

    public RetryWhenNetworkException() {
    }

    public RetryWhenNetworkException(int count) {
        this.count = count;
    }

    public RetryWhenNetworkException(int count, long delay) {
        this.count = count;
        this.delay = delay;
    }



    @Override
    public Publisher<?> apply(Flowable<? extends Throwable> flowable) throws Exception {
        return flowable
                .zipWith(Flowable.range(1, count + 1), new BiFunction<Throwable, Integer, Wrapper>() {
                    @Override
                    public Wrapper apply(Throwable throwable, Integer integer) throws Exception {
                        return new Wrapper(throwable,integer);
                    }
                }).flatMap((Function<Wrapper, Publisher<?>>) wrapper -> {
                    //如果没有打开网络那么直接返回
                    if (wrapper.throwable instanceof UnknownHostException) {
                        return Flowable.error(wrapper.throwable);
                    }
                    //如果网络异常是连接异常，超时异常那么重试
                    if ((wrapper.throwable instanceof ConnectException
                            || wrapper.throwable instanceof SocketTimeoutException
                            || wrapper.throwable instanceof TimeoutException)
                            && wrapper.index < count + 1) {
                        return Flowable.timer(delay + (wrapper.index - 1) * delay, TimeUnit.MILLISECONDS);
                    }
                    return Flowable.error(wrapper.throwable);
                });
    }

    private class Wrapper {
        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }
}

