package com.richfit.barcodesystemproduct.module.login;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.UserEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/10/27.
 */

public class LoginPresenterImp extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    LoginContract.View mView;

    @Override
    protected void onStart() {
        mView = getView();
    }

    @Inject
    public LoginPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void login(final String userName,final  String password) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            mView.loginFail("用户名或者密码为空");
            return;
        }

        ResourceSubscriber<UserEntity> subscriber = mRepository.Login(userName, password)
                .doOnNext(userEntity -> mRepository.saveUserInfo(userEntity))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<UserEntity>(mContext, "正在登陆...") {
                    @Override
                    public void _onNext(UserEntity userInfo) {
                        Global.LOGIN_ID = userInfo.loginId;
                        Global.USER_ID = userInfo.userId;
                        Global.USER_NAME = userInfo.userName;
                        Global.companyId = userInfo.companyId;
                        Global.companyCode = userInfo.companyCode;
                        Global.authOrg = userInfo.authOrgs;
                        Global.batchFlag = "Y".equals(userInfo.batchFlag) ? true : false;
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.networkConnectError(message);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.loginFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String msg) {
                        if (mView != null) {
                            mView.loginFail(msg);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.toHome();
                        }
                    }
                });

        addSubscriber(subscriber);
    }

    @Override
    public void readUserInfos() {
        mView = getView();
        mRepository.readUserInfo("", "")
                .filter(list -> list != null && list.size() > 0)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<ArrayList<String>>() {
                    @Override
                    public void onNext(ArrayList<String> list) {
                        if(mView != null) {
                            mView.showUserInfos(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if(mView != null) {
                            mView.loadUserInfosFai(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
