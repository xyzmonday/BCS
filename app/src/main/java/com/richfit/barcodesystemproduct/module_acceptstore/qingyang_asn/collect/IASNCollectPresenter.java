package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.collect;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2016/11/27.
 */

public interface IASNCollectPresenter extends IPresenter<IASNCollectView> {
    /**
     * 获取库存地点列表
     *
     * @param workId
     */
    void getInvsByWorks(String workId,int flag);
    /**
     * 获取数据采集界面的缓存
     * @param bizType：业务类型
     * @param materialNum：物资编码
     * @param userId：用户id
     * @param workId：发出工厂id
     * @param recWorkId：接收工厂id
     * @param recInvId：接收库存点id
     * @param batchFlag：发出批次
     */
    void getTransferSingleInfo(String bizType, String materialNum, String userId, String workId,
                               String invId, String recWorkId, String recInvId, String batchFlag,
                               String refDoc,int refDocItem);
    /**
     * 保存本次采集的数据
     *
     * @param result:用户采集的数据(json格式)
     */
    void uploadCollectionDataSingle(ResultEntity result);
}
