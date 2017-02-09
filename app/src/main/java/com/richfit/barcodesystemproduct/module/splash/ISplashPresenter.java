package com.richfit.barcodesystemproduct.module.splash;

import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2016/12/2.
 */

public interface ISplashPresenter extends IPresenter<ISplashView> {

    void syncDate();

    /**
     * 用户注册
     */
    void register();
}
