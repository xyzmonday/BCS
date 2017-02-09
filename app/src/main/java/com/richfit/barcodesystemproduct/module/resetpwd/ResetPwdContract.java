package com.richfit.barcodesystemproduct.module.resetpwd;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2017/1/20.
 */

public interface ResetPwdContract {
    interface View extends BaseView {
        void resetFail(String message);
        void resetSuccess();
    }

    interface Presenter extends IPresenter<View> {
        void changeLoginInfo(String userId,String newPwd);
    }
}
