package com.richfit.barcodesystemproduct.module_approval.baseheader;


import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2016/11/23.
 */

public interface IApprovalHeaderPresenter extends IPresenter<IApprovalHeaderView> {

    /**
     * 获取单据数据
     *
     * @param refNum：单号
     * @param refType:单据类型
     * @param bizType：业务类型
     * @param moveType:移动类型
     * @param userId：用户loginId
     */
    void getReference(String refNum, String refType,
                      String bizType, String moveType,
                      String refLineId,String userId);

    /**
     * 删除整单缓存
     *
     * @param refNum:单号
     * @param transId：缓存抬头id
     * @param refCodeId：单据抬头id
     * @param bizType：业务类型
     * @param userId：用户id
     */
    void deleteCollectionData(String refNum, String transId, String refCodeId,
                              String refType, String bizType, String userId,
                              String companyCode);
}
