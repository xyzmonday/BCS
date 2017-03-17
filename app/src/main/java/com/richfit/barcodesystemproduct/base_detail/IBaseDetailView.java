package com.richfit.barcodesystemproduct.base_detail;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * 所有的明细界面的View层接口
 * Created by monday on 2017/3/17.
 */

public interface IBaseDetailView extends BaseView {
    /**
     * 当明细页面可见时自动显示下拉刷新动画，并且开始加载明细数据
     */
    void startAutoRefresh();
    /**
     * 显示明细
     * @param allNodes
     */
    void showNodes(List<RefDetailEntity> allNodes);

    /**
     * 设置刷新动画是否结束
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
