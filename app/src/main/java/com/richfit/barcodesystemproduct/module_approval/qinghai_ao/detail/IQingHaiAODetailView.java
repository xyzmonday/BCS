package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * Created by monday on 2017/2/28.
 */

public interface IQingHaiAODetailView extends BaseView {
    /**
     * 开始获取缓存
     */
    void startAutoRefresh();

    /**
     * 显示明细
     *
     * @param nodes
     */
    void showNodes(List<RefDetailEntity> nodes,String transId);

    /**
     * 设置刷新动画是否结束
     *
     * @param isSuccess
     * @param message
     */
    void setRefreshing(boolean isSuccess, String message);

    /**
     * 删除父节点成功
     *
     * @param position：节点在明细列表的位置
     */
    void deleteNodeSuccess(int position);

    /**
     * 删除父节点失败
     *
     * @param message
     */
    void deleteNodeFail(String message);

    void showTransferedVisa(String transNum);
    /**
     * 数据提交到条码系统成功
     */
    void submitDataComplete();

    /**
     * 数据提交到条码系统失败
     * @param message
     */
    void submitDataFail(String message);
}
