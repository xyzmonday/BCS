package com.richfit.barcodesystemproduct.module.login;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;

import java.util.ArrayList;

/**
 * Created by monday on 2016/10/27.
 */

public interface LoginContract {

    interface View extends BaseView {
        //跳转到home界面
        void toHome();
        void loginFail(String message);
        //显示历史登陆用户
        void showUserInfos(ArrayList<String> list);
        void loadUserInfosFail(String message);
    }

    interface Presenter extends IPresenter<View> {
        void login(String userName, String password);

        void readUserInfos();
    }
}
