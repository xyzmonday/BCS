package com.richfit.barcodesystemproduct.module_locationadjust.collect.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_locationadjust.collect.ILACollectPresenter;
import com.richfit.barcodesystemproduct.module_locationadjust.collect.ILACollectView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/2/7.
 */

public class LACollectPresenterImp extends BasePresenter<ILACollectView>
        implements ILACollectPresenter {

    ILACollectView mView;

    @Inject
    public LACollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getMaterialInfo(String queryType, String materialNum) {
        mView = getView();

        RxSubscriber<MaterialEntity> subscriber = mRepository.getMaterialInfo(queryType, materialNum)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<MaterialEntity>(mContext, "正在获取物料信息...") {
                    @Override
                    public void _onNext(MaterialEntity materialEntity) {
                        if (mView != null) {
                            mView.getMaterialInfoSuccess(materialEntity);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.networkConnectError(Global.RETRY_QUERY_MATERIAL_INFO);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.getMaterialInfoFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.getMaterialInfoFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {

                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getInventoryInfo(String queryType, String workId, String invId, String workCode,
                                 String invCode, String storageNum, String materialNum,String materialId,
                                 String materialGroup, String materialDesc,
                                 String batchFlag, String location, String invType) {
        mView = getView();

        RxSubscriber<List<InventoryEntity>> subscriber =
                mRepository.getInventoryInfo(queryType, workId, invId, workCode, invCode, storageNum,
                        materialNum,materialId, materialGroup,
                        materialDesc, batchFlag, location, invType)
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<List<InventoryEntity>>(mContext, "正在获取库存信息...") {
                            @Override
                            public void _onNext(List<InventoryEntity> inventoryEntities) {
                                if (mView != null) {
                                    mView.getInventorySuccess(inventoryEntities.get(0));
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
                                    mView.getInventoryFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.getInventoryFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }


    @Override
    public void uploadCollectionDataSingle(ResultEntity result) {
        mView = getView();
        ResourceSubscriber<String> subscriber =
                mRepository.uploadCollectionDataSingle(result)
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<String>(mContext) {
                            @Override
                            public void _onNext(String s) {

                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.networkConnectError(Global.RETRY_SAVE_COLLECTION_DATA_ACTION);
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
        addSubscriber(subscriber);
    }

}
