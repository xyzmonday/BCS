package com.richfit.barcodesystemproduct.module_delivery.baseedit;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2016/11/21.
 */

public interface IDSEditPresenter extends IPresenter<IDSEditView> {
    /**
     * 获取库存信息
     *
     * @param workId:工厂id
     * @param invId：库存地点id
     * @param materialId：物料id
     * @param location：仓位
     * @param batchFlag:批次
     * @param invType：库存类型
     */
    void getInventoryInfo(String queryType, String workId, String invId,
                          String workCode, String invCode, String storageNum,
                          String materialNum,String materialId,
                          String location, String batchFlag, String invType);

    /**
     * 获取单条缓存
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

    /**
     * 保存本次采集的数据
     *
     * @param result:用户采集的数据(json格式)
     */
    void uploadCollectionDataSingle(ResultEntity result);

}
