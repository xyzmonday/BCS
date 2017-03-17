package com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.detail;

import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2017/3/16.
 */

public interface ILQDetailPresenter extends IPresenter<ILQDetailView> {

    /**
     * 获取库存信息
     * @param workId:工厂id
     * @param invId：库存地点id
     * @param materialId：物料id
     * @param location：仓位
     * @param batchFlag:批次
     * @param invType：库存类型
     */
    void getInventoryInfo(String queryType, String workId, String invId, String workCode,String invCode,
                          String storageNum,String materialNum,String materialId, String location, String batchFlag,
                          String specialInvFlag,String specialInvNum,String invType);

}
