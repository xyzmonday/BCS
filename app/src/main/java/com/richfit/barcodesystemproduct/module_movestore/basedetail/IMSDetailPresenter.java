package com.richfit.barcodesystemproduct.module_movestore.basedetail;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.Map;

/**
 * Created by monday on 2017/2/10.
 */

public interface IMSDetailPresenter extends IPresenter<IMSDetailView> {
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
     * 删除一个子节点
     *
     * @param lineDeleteFlag：是否删除整行(Y/N)
     * @param transId：缓存头id
     * @param transLineId:缓存行id
     * @param locationId:缓存仓位id
     * @param bizType:业务类型
     * @param position：该子节点在所有节点中的真实位置
     */
    void deleteNode(String lineDeleteFlag, String transId, String transLineId, String locationId,
                    String refType, String bizType, int position, String companyCode);

    /**
     * 修改子节点
     *
     * @param refData：单据数据
     * @param node：需要修改的子节点
     * @param ：subFunName子功能编码
     */
    void editNode(ReferenceEntity refData, RefDetailEntity node,
                  String companyCode, String bizType,
                  String refType, String subFunName);

    /**
     * 上传入库明细到条码系统
     *
     * @param transId：缓存头id
     * @param bizType:业务类型
     * @param voucherDate:过账日期
     */
    void submitData2BarcodeSystem(String transId, String bizType, String refType, String userId, String voucherDate,
                                  Map<String, Object> flagMap, Map<String, Object> extraHeaderMap, int submitFlag);

    /**
     * 提交数据到sap
     *
     * @param transId：缓存头id
     * @param bizType:业务类型
     * @param voucherDate:过账日期
     * @param userId：用户id
     */
    void submitData2SAP(String transId, String bizType, String refType, String userId, String voucherDate,
                        Map<String, Object> flagMap, Map<String, Object> extraHeaderMap, int submitFlag);

    /**
     * sap上下架处理
     *
     * @param transId
     * @param bizType
     * @param refType
     * @param userId
     * @param voucherDate
     * @param flagMap
     * @param extraHeaderMap
     * @param submitFlag
     */
    void sapUpAndDownLocation(String transId, String bizType, String refType, String userId, String voucherDate,
                              Map<String, Object> flagMap, Map<String, Object> extraHeaderMap, int submitFlag);

    /**
     * 数据提交到sap后，从数据明细界面跳转到抬头界面
     *
     * @param position
     */
    void showHeadFragmentByPosition(int position);
}
