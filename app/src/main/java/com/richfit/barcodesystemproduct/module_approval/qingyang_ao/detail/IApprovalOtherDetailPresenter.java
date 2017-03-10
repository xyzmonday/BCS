package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

/**
 * Created by monday on 2016/11/24.
 */

public interface IApprovalOtherDetailPresenter extends IPresenter<IApprovalOtherDetailView> {

    /**
     * 获取单据数据
     *
     * @param refNum：单号
     * @param refType:单据类型
     * @param bizType：业务类型
     * @param moveType:移动类型
     * @param userId：用户loginId
     */
    void getReference(ReferenceEntity refData, String refNum, String refType,
                      String bizType, String moveType,
                      String refLineId,String userId);

    /**
     * 父节点删除
     *
     * @param lineDeleteFlag:是否删除整行(该字段仅仅是针对具有父子节点结构的明细有作用)。如果Y那么一次性删除该福及诶单 下的所有子节点，如果是N那么仅仅删除一个子节点
     * @param refNum：单号
     * @param refLineNum：行号
     * @param refLineId：行id
     * @param refType：单据类型
     * @param userId：用户id
     */
    void deleteNode(String lineDeleteFlag, String refNum, String refLineNum, String refLineId,
                    String refType, String bizType, String userId, int position,
                    String companyCode);

    /**
     * 修改子节点
     *
     * @param node：需要修改的子节点
     * @param ：subFunName子功能编码
     */
    void editNode(RefDetailEntity node, String companyCode, String bizType,
                  String refType, String subFunName, int position);

    /**
     * 过账验收采集的数据
     *
     * @param refNum
     * @param refCodeId
     * @param voucherDate
     * @param userId
     * @param isLocal
     */
    void uploadCollectionData(String refNum, String refCodeId, String bizType, String refType, int inspectionType, String voucherDate,
                              String userId, boolean isLocal);


    /**
     * 数据提交到sap后，从数据明细界面跳转到抬头界面
     *
     * @param position
     */
    void showHeadFragmentByPosition(int position);
}
