package com.richfit.barcodesystemproduct.module.splash;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.LoadBasicDataWrapper;

import java.util.ArrayList;

/**
 * Created by monday on 2016/12/2.
 */

public interface ISplashPresenter extends IPresenter<ISplashView> {

    /**
     * 同步服务器时间
     */
    void syncDate();

    /**
     * 用户注册
     */
    void register();

    /**
     * 下载基础数据
     * @param requestParam
     */
    void loadAndSaveBasicData(ArrayList<LoadBasicDataWrapper> requestParam);
}
