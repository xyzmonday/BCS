package com.richfit.barcodesystemproduct.module_acceptstore.baseheader;

import android.support.annotation.NonNull;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ReferenceEntity;


/**
 * 物资入库抬头标准接口
 * Created by monday on 2016/11/11.
 */

public interface IASHeaderPresenter extends IPresenter<IASHeaderView> {
    /**
     * 获取单据数据
     *
     * @param refNum：单号
     * @param refType:单据类型
     * @param bizType：业务类型
     * @param moveType:移动类型
     * @param userId：用户loginId
     */
    void getReference(@NonNull String refNum, @NonNull String refType,
                      @NonNull String bizType, @NonNull String moveType,
                      @NonNull String refLineId,@NonNull String userId);
    /**
     * 获取整单缓存
     *
     * @param refData：抬头界面获取的单据数据
     * @param refCodeId：单据id
     * @param bizType:业务类型
     * @param refType：单据类型
     */
    void getTransferInfo(ReferenceEntity refData, String refCodeId, String bizType, String refType);

    /**
     * 删除整单缓存
     *
     * @param refNum:单据号
     * @param transId:缓存抬头id
     * @param bizType:业务类型
     */
    void deleteCollectionData(String refNum, String transId, String refCodeId, String refType, String bizType,
                              String userId, String companyCode);

//    /**
//     * 获取发出工厂列表
//     */
//    void getWorks();
//
//    /**
//     * 通过工厂id获取该工厂下的接收库存地点列表
//     * @param workId
//     */
//    void getInvsByWorkId(@NonNull String workId);
}
