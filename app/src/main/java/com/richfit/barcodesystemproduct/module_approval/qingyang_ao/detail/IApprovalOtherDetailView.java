package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * Created by monday on 2016/11/24.
 */

public interface IApprovalOtherDetailView extends BaseView {

    /**
     * 开始获取缓存
     */
    void startAutoRefresh();
    /**
     * 显示明细
     * @param nodes
     */
    void showNodes(List<RefDetailEntity> nodes);

    /**
     * 设置刷新动画是否结束
     * @param isSuccess
     * @param message
     */
    void setRefreshing(boolean isSuccess, String message);

    /**
     * 删除父节点成功
     * @param position：节点在明细列表的位置
     */
    void deleteNodeSuccess(int position);

    /**
     * 删除父节点失败
     * @param message
     */
    void deleteNodeFail(String message);
    /**
     * 数据提交到条码系统成功
     */
    void showSubmitSuccessMessage(String message);

    /**
     * 数据提交到条码系统失败
     * @param message
     */
    void submitDataFail(String message);

}
