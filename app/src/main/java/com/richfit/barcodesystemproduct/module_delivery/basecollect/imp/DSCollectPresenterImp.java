package com.richfit.barcodesystemproduct.module_delivery.basecollect.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.IDSCollectPresenter;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.IDSCollectView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/19.
 */

public class DSCollectPresenterImp extends BasePresenter<IDSCollectView>
        implements IDSCollectPresenter {

    IDSCollectView mView;

    @Inject
    public DSCollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getInvsByWorkId(String workId,int flag) {
        mView = getView();
        ResourceSubscriber<ArrayList<InvEntity>> subscriber =
                mRepository.getInvsByWorkId(workId,flag)
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<ArrayList<InvEntity>>() {
                            @Override
                            public void onNext(ArrayList<InvEntity> list) {
                                if (mView != null) {
                                    mView.showInvs(list);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.loadInvFail(t.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }

    @Override
    public void getInventoryInfo(String queryType, String workId, String invId,String workCode,String invCode,String storageNum,
                                 String materialNum,String materialId, String location, String batchFlag, String invType) {
        mView = getView();
        RxSubscriber<List<InventoryEntity>> subscriber = mRepository.getInventoryInfo(queryType, workId, invId,
                workCode,invCode,storageNum,materialNum, materialId, "", "", batchFlag, location, invType)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<List<InventoryEntity>>(mContext) {
                    @Override
                    public void _onNext(List<InventoryEntity> list) {
                        if (mView != null) {
                            mView.showInventory(list);
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

                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getTransferInfoSingle(String refCodeId, String refType, String bizType, String refLineId,
                                      String batchFlag, String location, String userId) {
        mView = getView();
        RxSubscriber<RefDetailEntity> subscriber =
                mRepository.getTransferInfoSingle(refCodeId, refType, bizType, refLineId, "", "", "", "", "", batchFlag, location, userId)
                        .filter(refData -> refData != null && refData.billDetailList != null)
                        .flatMap(refData -> getMatchedLineData(refLineId, refData))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<RefDetailEntity>(mContext) {
                            @Override
                            public void _onNext(RefDetailEntity cache) {
                                //获取缓存数据
                                if (mView != null) {
                                    mView.onBindCache(cache, batchFlag, location);
                                }
                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.networkConnectError(Global.RETRY_LOAD_SINGLE_CACHE_ACTION);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if (mView != null) {
                                    mView.loadCacheFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.loadCacheFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {
                                if (mView != null) {
                                    mView.loadCacheSuccess();
                                }
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
