package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.collect.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.collect.IQingHaiRSNCollectPresenter;
import com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.collect.IQingHaiRSNCollectView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/3/2.
 */

public class QingHaiRSNCollectPresenterImp extends BasePresenter<IQingHaiRSNCollectView>
        implements IQingHaiRSNCollectPresenter {

    IQingHaiRSNCollectView mView;

    @Inject
    public QingHaiRSNCollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }


    @Override
    public void getInvsByWorks(String workId,int flag) {
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
                                    mView.loadInvsFail(t.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }

    @Override
    public void getTransferSingleInfo(String bizType, String materialNum, String userId, String workId,
                                      String invId, String recWorkId, String recInvId, String batchFlag) {
        mView = getView();
        RxSubscriber<ReferenceEntity> subscriber = mRepository.getTransferInfoSingle("","",bizType, "",
                workId,invId,recWorkId,recInvId,materialNum,batchFlag,"",userId)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<ReferenceEntity>(mContext) {
                    @Override
                    public void _onNext(ReferenceEntity refData) {
                        if(mView != null) {
                            mView.onBindCommonUI(refData,batchFlag);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if(mView != null) {
                            mView.networkConnectError(Global.RETRY_LOAD_SINGLE_CACHE_ACTION);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if(mView != null) {
                            mView.loadTransferSingleInfoFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if(mView != null) {
                            mView.loadTransferSingleInfoFail(message);
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
                                if(mView != null) {
                                    mView.networkConnectError(Global.RETRY_SAVE_COLLECTION_DATA_ACTION);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if(mView != null) {
                                    mView.saveCollectedDataFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if(mView != null) {
                                    mView.saveCollectedDataFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {
                                if(mView != null) {
                                    mView.saveCollectedDataSuccess();
                                }
                            }
                        });
        addSubscriber(subscriber);
    }
}
