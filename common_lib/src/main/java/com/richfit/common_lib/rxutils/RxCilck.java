package com.richfit.common_lib.rxutils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.Observable;


/**
 * Created by monday on 2016/11/13.
 */

public class RxCilck {

    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        return RxView.clicks(view)
                .throttleFirst(500, TimeUnit.MILLISECONDS);
    }
}
