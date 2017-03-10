package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.detail;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

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
    }
}
