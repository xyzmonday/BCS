package com.richfit.barcodesystemproduct.module.setting;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.LoadBasicDataWrapper;

import java.util.ArrayList;

/**
 * Created by monday on 2016/11/29.
 */

public interface ISettingPresenter extends IPresenter<ISettingView> {
    /**
     * 下载基础数据
     * @param requestParam
     */
    void loadAndSaveBasicData(ArrayList<LoadBasicDataWrapper> requestParam);

    /**
     * 获取最新版本的信息
     */
    void getAppVersion();

    /**
     * 下载最新的app
     * @param url：下载网址
     * @param saveName：保存的名字
     * @param savePath：保存的路径
     */
    void loadLatestApp(String url, String saveName, String savePath);
    //暂停下载app
    void pauseLoadApp();
}
