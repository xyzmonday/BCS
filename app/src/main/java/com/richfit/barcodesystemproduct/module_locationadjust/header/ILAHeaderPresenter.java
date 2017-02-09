package com.richfit.barcodesystemproduct.module_locationadjust.header;

import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2017/2/7.
 */

public interface ILAHeaderPresenter extends IPresenter<ILAHeaderView> {

    void getWorks(int flag);

    void getInvsByWorkId(String workId,int flag);

    void getStorageNum(String workId,String workCode,String invId,String invCode);
}
