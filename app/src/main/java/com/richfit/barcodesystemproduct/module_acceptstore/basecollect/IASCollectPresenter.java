package com.richfit.barcodesystemproduct.module_acceptstore.basecollect;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;


/**
 * Created by monday on 2016/11/15.
 */

public interface IASCollectPresenter extends IPresenter<IASCollectView> {

    /**
     * 通过工厂id获取该工厂下的库存地点列表
     *
     * @param workId
     */
    void getInvsByWorkId(String workId,int flag);


    void checkLocation(String queryType, String workId, String invId, String batchFlag, String location);

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
                               String batchFlag, String location,String refDoc,int refDocItem, String userId);

    /**
     * 保存本次采集的数据
     *
     * @param result:用户采集的数据(json格式)
     */
    void uploadCollectionDataSingle(ResultEntity result);

}
