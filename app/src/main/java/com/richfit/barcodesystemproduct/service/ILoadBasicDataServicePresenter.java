package com.richfit.barcodesystemproduct.service;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.LoadBasicDataWrapper;

import java.util.ArrayList;

/**
 * Created by monday on 2016/11/16.
 */

public interface ILoadBasicDataServicePresenter extends IPresenter<ILoadBasicDataServiceView>{
    /**
     * 下载基础数据
     * @param requestParam
     */
    void loadAndSaveBasicData(ArrayList<LoadBasicDataWrapper> requestParam);
}
