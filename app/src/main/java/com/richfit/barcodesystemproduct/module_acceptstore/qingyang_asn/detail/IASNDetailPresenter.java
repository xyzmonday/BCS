package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.detail;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by monday on 2016/11/27.
 */

public interface IASNDetailPresenter extends IPresenter<IASNDetailView> {
    /**
     * 获取整单缓存
     *
     * @param bizType：业务类型
     * @param userId：用户id
     * @param workId：发出工厂id
     * @param invId:发出库存地点id
     * @param recWorkId：接收工厂id
     * @param recInvId：接搜库存地点id
     */
    void getTransferInfo(String refCodeId, String bizType, String refType, String userId,
                         String workId, String invId, String recWorkId, String recInvId);

    /**
     * 子节点修改
     *
     * @param sendLocations：发出仓位集合
     * @param recLocations：接收仓位集合
     * @param node：需要修改的子节点
     * @param companyCode
     * @param subFunName
     */
    void editNode(ArrayList<String> sendLocations, ArrayList<String> recLocations,
                  RefDetailEntity node, String companyCode,
                  String bizType, String refType, String subFunName);

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
     * 上传入库明细到条码系统
     *
     * @param transId：缓存头id
     * @param bizType:业务类型
     * @param voucherDate:过账日期
     */
    void submitData2BarcodeSystem(String transId, String bizType, String refType, String voucherDate);

    /**
     * 提交数据到sap
     *
     * @param transId：缓存头id
     * @param bizType:业务类型
     * @param voucherDate:过账日期
     * @param userId：用户id
     */
    void submitData2SAP(String transId, String bizType, String refType, String userId, String voucherDate, Map<String, Object> extraHeaderMap);

    /**
     * 数据提交到sap后，从数据明细界面跳转到抬头界面
     *
     * @param position
     */
    void showHeadFragmentByPosition(int position);
}
