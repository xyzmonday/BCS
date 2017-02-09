package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.collect;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2016/11/23.
 */

public interface IApprovalOtherPresenter extends IPresenter<IApprovalOtherView> {

    /**
     * 获取单条缓存。
     *
     * @param refCodeId：单据id
     * @param refType：单据类型
     * @param bizType：业务类型
     * @param refLineId：单据行id
     * @param batchFlag:批次
     * @param location：仓位
     */
    void getTransferInfoSingle(String refCodeId, String refType, String bizType, String refLineId,
                               String batchFlag, String location, String userId);

    void getInvsByWorkId(String workId,int flag);

    /**
     * 上传本次采集验收数据
     * @param result
     */
    void uploadInspectionDataSingle(ResultEntity result);

}
