package com.richfit.barcodesystemproduct.module.edit;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.BizFragmentConfig;

/**
 * Created by monday on 2017/1/13.
 */

public interface IEditContract {
    interface View extends BaseView {
        void showEditFragment(BizFragmentConfig fragmentConfig);

        void initEditFragmentFail(String message);
    }

    interface Presenter extends IPresenter<View> {
        void setupEditFragment(String bizType, String refType, int fragemntType);
    }
}
