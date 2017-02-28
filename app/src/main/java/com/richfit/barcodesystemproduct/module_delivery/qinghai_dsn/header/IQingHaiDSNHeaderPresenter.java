package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.header;

import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2017/2/23.
 */

public interface IQingHaiDSNHeaderPresenter extends IPresenter<IQingHaiDSNHeaderView>{

    void getWorks(int flag);
    void getCostCenterList(String workCode,String keyWord,int defaultItemNum,int flag);
    /**
     * 删除整单缓存
     * @param bizType：业务类型
     * @param userId:用户id
     */
    void deleteCollectionData(String refType, String bizType, String userId,
                              String companyCode);
}
