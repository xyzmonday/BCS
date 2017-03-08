package com.richfit.barcodesystemproduct.module_check.qinghai_blind.collect.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_check.qinghai_blind.collect.IBlindCollectPresenter;
import com.richfit.barcodesystemproduct.module_check.qinghai_blind.collect.IBlindCollectView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.ResultEntity;

import javax.inject.Inject;

/**
 * Created by monday on 2017/3/3.
 */

public class BlindCollectPresenterImp extends BasePresenter<IBlindCollectView>
        implements IBlindCollectPresenter {

    IBlindCollectView mView;

    @Inject
    public BlindCollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getCheckTransferInfoSingle(final String checkId, String location, String queryType,
                                           String materialNum, String bizType) {
        mView = getView();

        RxSubscriber<MaterialEntity> subscriber =
                mRepository.getMaterialInfo(queryType, materialNum)
                        .filter(materialEntity -> materialEntity != null && !TextUtils.isEmpty(materialEntity.id))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<MaterialEntity>(mContext, "正在获取盘点库存信息...") {
                            @Override
                            public void _onNext(MaterialEntity data) {
                                if (mView != null) {
                                    mView.loadMaterialInfoSuccess(data);
                                }
                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.networkConnectError(Global.RETRY_LOAD_INVENTORY_ACTION);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if (mView != null) {
                                    mView.loadMaterialInfoFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.loadMaterialInfoFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }

    @Override
    public void uploadCheckDataSingle(ResultEntity result) {
        mView = getView();
        mRepository.uploadCheckDataSingle(result)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在保存本次盘点数量...") {
                    @Override
                    public void _onNext(String s) {

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
                            mView.saveCollectedDataFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.saveCollectedDataFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.saveCollectedDataSuccess();
                        }
                    }
                });
    }
}
