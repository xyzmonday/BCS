package com.richfit.barcodesystemproduct.module_movestore.baseheader_n;

/**
 * Created by monday on 2016/11/20.
 */

public interface INMSHeaderPresenter {

    /**
     * 获取发出工厂列表
     */
    void getWorks(int flag);

    /**
     * 通过工厂id获取该工厂下的接收库存地点列表
     * @param workId
     */
    void getRecInvsByWorkId(String workId,int flag);

    void getSendInvsByWorkId(String workId,int flag);

    /**
     * 删除整单缓存
     * @param bizType：业务类型
     * @param userId:用户id
     */
    void deleteCollectionData(String refType, String bizType, String userId,
                              String companyCode);
}
