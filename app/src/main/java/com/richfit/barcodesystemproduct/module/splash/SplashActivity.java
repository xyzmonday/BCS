package com.richfit.barcodesystemproduct.module.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.barcodesystemproduct.module.login.LoginActivity;
import com.richfit.barcodesystemproduct.module.splash.imp.SplashPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Flowable;

/**
 * 在启动app的时候，如果一个Activity所属的Application还没运行，系统会为这个Activity创建一个
 * 进程（每开启一个进程都会有一个Application，所以Application的onCreate()可能会被调用多次），
 * 但是启动一个进程需要时间，所以如果该进程初始化耗时较长，那么屏幕没有任何动静。
 * 所以，系统会自动启动一个startingWindow。StartingWindow一般出现在应用程序进程创建并初始化成功前，所以它是个临时窗口，对应的WindowType是TYPE_APPLICATION_STARTING。
 * 目的是告诉用户，系统已经接受到操作，正在响应，在程序初始化完成后实现目的UI，同时移除这个窗口。
 * 系统给startingWindow的DecorView设置Activity或者Application的Theme。
 * 所以，我们启动一个Splash页面，这里需要注意的是Splash页面不能够有耗时操作。
 * 因为如果Splash有耗时操作，那么startingWindow结束显示后，会显示Splash的
 * Window(注意Splash页面没有setContentView)。所以SplashActivity中不要做太多操作，如果确实要做的话，我想到的解决方式是：
 * 将两个的window的background设置的一样，这样即便startingWindow结束，用户也感觉不到切换了。
 * 设置SplashActivity对应window的background的方法，在SplashActivity的onCreate中调用
 * getWindow().setBackgroundDrawableResource(背景图片);
 * Created by monday on 2016/12/2.
 */

public class SplashActivity extends BaseActivity<SplashPresenterImp>
        implements ISplashView {

    @Override
    protected int getContentId() {
        return 0;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBar(false);
        super.onCreate(savedInstanceState);
        Flowable.just(1)
                .map(a->initAppConfig(getApplicationContext()))
                .compose(TransformerHelper.io2main())
                .subscribe(flag-> mPresenter.register());
    }

    private boolean initAppConfig(Context context) {
        Global.macAddress = UiUtil.getMacAddress();
        Global.serialNum = UiUtil.getDeviceId(context.getApplicationContext());
        return true;
    }

    @Override
    public void toLogin() {
        Flowable.timer(1000, TimeUnit.MILLISECONDS)
                .compose(TransformerHelper.io2main())
                .subscribe(a -> {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(0, android.R.anim.fade_out);
                    finish();
                });
    }

    @Override
    public void unRegister(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("提示")
                .setContentText(message + "。请点击立即注册获取手持MAC和序列号进行注册")
                .setConfirmText("立即注册")
                .setCancelText("下次再说")
                .setConfirmClickListener(sDialog -> {
                    sDialog.setTitleText("用户注册")
                            .setContentText("手持的MAC地址:" + Global.macAddress + ";\n" + "手持的序列号:" + Global.serialNum)
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                })
                .show();
    }

    @Override
    public void registered() {
        mPresenter.syncDate();
    }

    @Override
    public void syncDateSuccess(String dateStr) {
        SPrefUtil.saveData(Global.SYNC_DATE_KEY, dateStr);
    }

    @Override
    public void syncDateFail(String message) {
        showMessage(message);
    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(retryAction);
    }

    @Override
    public void retry(String action) {
        switch (action) {
            case Global.RETRY_REGISTER_ACTION:
                mPresenter.register();
                break;
        }
        //回调父方法关闭对话框
        super.retry(action);
    }
}
