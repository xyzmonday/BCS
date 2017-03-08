package com.richfit.barcodesystemproduct.module_check.qinghai_cn.detail.imp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module.main.MainActivity;
import com.richfit.barcodesystemproduct.module_check.qinghai_cn.detail.ICNDetailPresenter;
import com.richfit.barcodesystemproduct.module_check.qinghai_cn.detail.ICNDetailView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/12/6.
 */

public class CNDetailPresenterImp extends BasePresenter<ICNDetailView>
        implements ICNDetailPresenter {

    ICNDetailView mView;

    @Inject
    public CNDetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getCheckTransferInfo(String checkId, String materialNum, String location, String isPageQuery,
                                     int pageNum, int pageSize, String bizType) {
        mView = getView();
        ResourceSubscriber<ReferenceEntity> subscriber =
                mRepository.getCheckTransferInfo(checkId, materialNum, location, isPageQuery, pageNum, pageSize, bizType)
                        .filter(refData -> refData != null && refData.checkList != null)
                        .map(refData -> setCheckFlag(refData))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<ReferenceEntity>() {
                            @Override
                            public void onNext(ReferenceEntity refData) {
                                if (mView != null) {
                                    mView.showCheckInfo(refData, pageNum);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.loadCheckInfoFail(t.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }

    @Override
    public void deleteNode(String checkId, String checkLineId, String userId, int position, String bizType) {
        mView = getView();
        mRepository.deleteCheckDataSingle(checkId, checkLineId, userId, bizType)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在删除...") {
                    @Override
                    public void _onNext(String s) {

                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {

                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.deleteNodeFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.deleteNodeFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.deleteNodeSuccess(position);
                        }
                    }
                });
    }

    @Override
    public void editNode(InventoryEntity node, String companyCode, String bizType,
                         String refType, String subFunName) {

        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(Global.EXTRA_COMPANY_CODE_KEY, companyCode);
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);

        //该子节点的id
        bundle.putString(Global.EXTRA_REF_LINE_ID_KEY, node.checkLineId);
        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, "物资盘点-明细修改");
        //物料
        bundle.putString(Global.EXTRA_MATERIAL_NUM_KEY, node.materialNum);
        bundle.putString(Global.EXTRA_MATERIAL_ID_KEY, node.materialId);
        //物料描述
        bundle.putString(Global.EXTRA_MATERIAL_DESC_KEY, node.materialDesc);
        //物料组
        bundle.putString(Global.EXTRA_MATERIAL_GROUP_KEY, node.materialGroup);
        //特殊库存标识
        bundle.putString(Global.EXTRA_SPECIAL_INV_FLAG_KEY, node.specialInventoryFlag);
        bundle.putString(Global.EXTRA_SPECIAL_INV_NUM_KEY, node.specialInventoryNum);
        //工厂和库存地点
        bundle.putString(Global.EXTRA_WORK_ID_KEY,node.workId);
        bundle.putString(Global.EXTRA_INV_ID_KEY,node.invId);
        //库存
        bundle.putString(Global.EXTRA_INV_QUANTITY_KEY, node.invQuantity);
        //需要修改的字段
        bundle.putString(Global.EXTRA_QUANTITY_KEY, node.totalQuantity);
        bundle.putString(Global.EXTRA_LOCATION_KEY, node.location);

        intent.putExtras(bundle);

        Activity activity = (Activity) mContext;
        activity.startActivity(intent);
    }

    @Override
    public void transferCheckData(String checkId, String userId, String bizType) {
        mView = getView();
        RxSubscriber<String> subscriber = mRepository.transferCheckData(checkId, userId, bizType)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在过账...") {
                    @Override
                    public void _onNext(String s) {
                        if(mView != null) {
                            mView.showTransferedNum(s);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.networkConnectError(Global.RETRY_TRANSFER_DATA_ACTION);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.transferCheckDataFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.transferCheckDataFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.transferCheckDataSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    /**
     * 设置该行是否已经盘点标志
     *
     * @param refData
     * @return
     */
    private ReferenceEntity setCheckFlag(ReferenceEntity refData) {
        List<InventoryEntity> checkList = refData.checkList;
        for (InventoryEntity entity : checkList) {
            if (!TextUtils.isEmpty(entity.totalQuantity) && !"0".equals(entity.totalQuantity)) {
                entity.isChecked = true;
            }
        }
        return refData;
    }


    @Override
    public void showHeadFragmentByPosition(int position) {
        if (MainActivity.class.isInstance(mContext)) {
            MainActivity activity = (MainActivity) mContext;
            activity.showFragmentByPosition(position);
            mRxManager.post(Global.CLEAR_HEADER_UI, true);
        }
    }
}
