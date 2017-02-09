package com.richfit.barcodesystemproduct.module.main;

import android.support.v4.app.FragmentManager;

import com.richfit.barcodesystemproduct.adapter.MainPagerViewAdapter;
import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;


/**
 * Created by monday on 2016/11/10.
 */

public interface MainContract {

    interface View extends BaseView {

        void showMainContent(MainPagerViewAdapter adapter, int currentPageIndex);
        void setupMainContentFail(String message);
    }

    interface Presenter extends IPresenter<View> {
        void setupMainContent(FragmentManager fragmentManager, String companyCode, String moduleCode,
                              String bizType, String refType, int currentPageIndex);
    }
}
