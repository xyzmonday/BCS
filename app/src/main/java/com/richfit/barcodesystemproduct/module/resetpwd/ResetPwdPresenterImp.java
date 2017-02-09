package com.richfit.barcodesystemproduct.module.resetpwd;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;

import javax.inject.Inject;

/**
 * Created by monday on 2017/1/20.
 */

public class ResetPwdPresenterImp extends BasePresenter<ResetPwdContract.View> implements ResetPwdContract.Presenter{


    @Inject
    public ResetPwdPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void changeLoginInfo(String userId, String newPwd) {

    }
}
