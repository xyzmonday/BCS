package com.richfit.barcodesystemproduct.module.welcome;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2016/11/8.
 */

public interface WelcomeContract {

    interface View extends BaseView {

        void loadConfigSuccess(int mode);
        void loadConfigFail(String message);

    }

    interface Presenter extends IPresenter<View> {
        //下载配置文件
        void loadConfig(String companyId, int mode);
        //跳转到Home页面
        void toHome(int mode);
    }
}
