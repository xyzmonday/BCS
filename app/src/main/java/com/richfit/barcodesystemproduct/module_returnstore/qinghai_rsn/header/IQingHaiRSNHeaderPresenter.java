package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.header;

import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2017/3/2.
 */

public interface IQingHaiRSNHeaderPresenter extends IPresenter<IQingHaiRSNHeaderView>{
    void getWorks(int flag);
    void getAutoCompleteList(String workCode,String keyWord,int defaultItemNum,int flag,String bizType);
    /**
     * 删除整单缓存
     * @param bizType：业务类型
     * @param userId:用户id
     */
    void deleteCollectionData(String refType, String bizType, String userId,
                              String companyCode);
}
