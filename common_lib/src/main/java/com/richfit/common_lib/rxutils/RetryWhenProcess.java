package com.richfit.common_lib.rxutils;

import com.richfit.common_lib.utils.L;

import org.reactivestreams.Publisher;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RetryWhenProcess implements Function<Flowable<Throwable>, Publisher<?>> {

    private long mInterval;
    private int mRetryCount = 0;
    private int mMaxRetries;

    public RetryWhenProcess(long interval, int maxRetries) {
        mInterval = interval;
        mMaxRetries = maxRetries;
    }


    @Override
    public Publisher<?> apply(Flowable<Throwable> flowable) throws Exception {
        return flowable.flatMap(throwable -> {
            if (throwable instanceof UnknownHostException) {
                return Flowable.error(throwable);
            }
            return Flowable.just(throwable)
                    .flatMap((Function<Throwable, Publisher<?>>) t -> {
                        if (++mRetryCount <= mMaxRetries) {
                            long delay = (long) Math.pow(mInterval, mRetryCount);
                            L.d("get error,system will retry after" + delay + "; retry count " + mRetryCount);
                            return Flowable.timer(delay, TimeUnit.MILLISECONDS);
                        }
                        return Flowable.error(t);
                    });
        });
    }
}