package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.detail;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2017/3/10.
 */

public interface QingHaiWWCDetailContract {

    interface IQingHaiWWCDetailView extends BaseView {
        /**
         * 开始获取缓存
         */
        void startAutoRefresh();

        /**
         * 显示明细
         *
         * @param allNodes
         */
        void showNodes(List<RefDetailEntity> allNodes);

        /**
         * 设置刷新动画是否结束
         *
         * @param isSuccess
         * @param message
         */
        void setRefreshing(boolean isSuccess, String message);
        /**
         * 删除子节点成功
         * @param position：节点在明细列表的位置
         */
        void deleteNodeSuccess(int position);

        /**
         * 删除子节点失败
         * @param message
         */
        void deleteNodeFail(String message);
        /**
         * 显示过账成功后的凭证
         * @param visa
         */
        void showTransferedVisa(String visa);
        /**
         * 数据提交到条码系统成功
         */
        void submitBarcodeSystemSuccess();

        /**
         * 数据提交到条码系统失败
         * @param message
         */
        void submitBarcodeSystemFail(String message);

        void showInspectionNum(String message);

        /**
         * 数据提交到SAP成功
         */
        void submitSAPSuccess();
        /**
         * 数据提交到SAP失败
         */
        void submitSAPFail(String[] messages);

        void upAndDownLocationFail(String[] messages);
        void upAndDownLocationSuccess();
    }

    interface IQingHaiWWCDetailPresenter extends IPresenter<IQingHaiWWCDetailView> {
        /**
         * 获取整单缓存
         *
         * @param refNum：单据号
         * @param refCodeId：单据id
         * @param bizType:业务类型
         * @param refType：单据类型
         */
        void getTransferInfo(String refNum, String refCodeId, String bizType, String refType,
                             String moveType,String refLineId,String userId);
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
         * @param locations:对于没有父子节点的明细需要传入已经上架的仓位
         * @param refData：单据数据
         * @param node：需要修改的子节点
         * @param ：subFunName子功能编码
         */
        void editNode(ArrayList<String> locations, ReferenceEntity refData, RefDetailEntity node, String companyCode,
                      String bizType, String refType, String subFunName, int position);
        /**
         * 上传入库明细到条码系统
         *
         * @param transId：缓存头id
         * @param bizType:业务类型
         * @param voucherDate:过账日期
         */
        void submitData2BarcodeSystem(String transId, String bizType, String refType, String userId, String voucherDate,
                                      Map<String, Object> flagMap, Map<String, Object> extraHeaderMap);

        /**
         * 提交数据到sap
         *
         * @param transId：缓存头id
         * @param bizType:业务类型
         * @param voucherDate:过账日期
         * @param userId：用户id
         */
        void submitData2SAP(String transId, String bizType, String refType, String userId, String voucherDate,
                            Map<String, Object> flagMap, Map<String, Object> extraHeaderMap);

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


    }
}
