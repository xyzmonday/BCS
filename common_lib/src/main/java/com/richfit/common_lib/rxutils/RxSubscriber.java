package com.richfit.common_lib.rxutils;

import android.content.Context;

import com.richfit.common_lib.exception.ResponseNullException;
import com.richfit.common_lib.exception.ServerException;
import com.richfit.common_lib.utils.LoadingLayoutHelper;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 封装Flowable的订阅
 * Created by monday on 2016/10/26.
 */
public abstract class RxSubscriber<T> extends ResourceSubscriber<T> {

    private WeakReference<Context> mWeakContext;
    private String msg;

    public RxSubscriber(Context context, String msg) {
        this.mWeakContext = new WeakReference<>(context);
        this.msg = msg;
    }

    public RxSubscriber(Context context) {
        this(context,"正在玩命加载...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Context context = mWeakContext.get();
        if (context != null)
            LoadingLayoutHelper.showDialogForLoading(context, msg);
//
//        if(context != null) {
//            StyledDialog.buildLoading(context,msg).show();
//        }
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable throwable) {
        LoadingLayoutHelper.cancelDialogForLoading();
//        StyledDialog.dismissLoading();
        //网络异常
        if (throwable instanceof ConnectException ||
                throwable instanceof SocketTimeoutException ||
                throwable instanceof TimeoutException) {
            _onNetWorkConnectError(throwable.getMessage());
            //如果没有打开网络
        } else if (throwable instanceof UnknownHostException) {
            _onCommonError(throwable.getMessage());
        } else {
            if (throwable instanceof ResponseNullException) {
                _onCommonError("数据返回为空");
            } else if (throwable instanceof ServerException) {
                _onServerError(((ServerException) throwable).getReturnCode(), throwable.getMessage());
            } else {
                _onCommonError(throwable.getMessage());
            }
        }
    }

    @Override
    public void onComplete() {
        LoadingLayoutHelper.cancelDialogForLoading();
//        StyledDialog.dismissLoading();
        _onComplete();
    }

    public abstract void _onNext(T t);//onNext()

    public abstract void _onNetWorkConnectError(String message);

    //其他错误（非网络）
    public abstract void _onCommonError(String message);

    public abstract void _onServerError(String code, String message);//接口调用操作出现异常，比如注册失败（已注册,短信验证码出错,and so on）

    public abstract void _onComplete();//onComplete()

}
