package com.richfit.barcodesystemproduct.module.splash.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module.splash.ISplashPresenter;
import com.richfit.barcodesystemproduct.module.splash.ISplashView;
import com.richfit.barcodesystemproduct.service.LoadBasicDataService;
import com.richfit.common_lib.rxutils.RxManager;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/12/2.
 */

public class SplashPresenterImp extends BasePresenter<ISplashView>
        implements ISplashPresenter {

    ISplashView mView;

    @Inject
    public SplashPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        mRxManager = RxManager.getInstance();
        mRxManager.register(Global.LOAD_BASIC_DATA_COMPLETE, a -> mView.toLogin());
    }

    @Override
    public void syncDate() {
        mView = getView();
        addSubscriber(mRepository.syncDate()
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        if (mView != null) {
                            mView.syncDateSuccess(s);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.syncDateFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        //下载组织机构基础数据
                        LoadBasicDataService.startService(mContext.getApplicationContext());
                    }
                }));
    }

    @Override
    public void register() {
        mView = getView();

        ResourceSubscriber<String> subscriber = mRepository.getMappingInfo()
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext,"正在初始化系统...") {
                    @Override
                    public void _onNext(String s) {

                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if(mView != null) {
                            mView.networkConnectError(Global.RETRY_REGISTER_ACTION);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if(mView != null) {
                            mView.unRegister(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if(mView != null) {
                            mView.unRegister(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if(mView != null) {
                            mView.registered();
                        }
                    }
                });
        addSubscriber(subscriber);
    }
}
