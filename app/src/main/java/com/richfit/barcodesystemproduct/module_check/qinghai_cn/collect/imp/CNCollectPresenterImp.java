package com.richfit.barcodesystemproduct.module_check.qinghai_cn.collect.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_check.qinghai_cn.collect.ICNCollectPresenter;
import com.richfit.barcodesystemproduct.module_check.qinghai_cn.collect.ICNCollectView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by monday on 2017/3/3.
 */

public class CNCollectPresenterImp extends BasePresenter<ICNCollectView>
        implements ICNCollectPresenter {

    ICNCollectView mView;

    @Inject
    public CNCollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getCheckTransferInfoSingle(final String checkId, String location, String queryType,
                                           String materialNum, String bizType) {
        mView = getView();

        RxSubscriber<List<InventoryEntity>> subscriber =
                mRepository.getMaterialInfo(queryType, materialNum)
                        .filter(materialEntity -> materialEntity != null && !TextUtils.isEmpty(materialEntity.id))
                        .flatMap(materialEntity -> mRepository.getCheckTransferInfoSingle(checkId, materialEntity.id, materialEntity.materialNum, location, bizType))
                        .filter(list -> list != null && list.size() > 0)
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<List<InventoryEntity>>(mContext, "正在获取盘点库存信息...") {
                            @Override
                            public void _onNext(List<InventoryEntity> list) {
                                if (mView != null) {
                                    mView.loadInventorySuccess(list);
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
                                    mView.loadInventoryFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.loadInventoryFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {
                                if (mView != null) {
                                    mView.loadInventoryComplete();
                                }
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
