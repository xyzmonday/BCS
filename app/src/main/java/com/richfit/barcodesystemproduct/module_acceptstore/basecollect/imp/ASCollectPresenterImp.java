package com.richfit.barcodesystemproduct.module_acceptstore.basecollect.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.IASCollectPresenter;
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.IASCollectView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/15.
 */

public class ASCollectPresenterImp extends BasePresenter<IASCollectView>
        implements IASCollectPresenter {

    protected IASCollectView mView;

    @Inject
    public ASCollectPresenterImp(@ContextLife("Activity") Context context) {
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
    public void checkLocation(String queryType, String workId, String invId, String batchFlag, String location) {
        mView = getView();
        if (TextUtils.isEmpty(workId) && mView != null) {
            mView.checkLocationFail("工厂为空");
            return;
        }

        if (TextUtils.isEmpty(invId) && mView != null) {
            mView.checkLocationFail("库存地点为空");
            return;
        }

        ResourceSubscriber<String> subscriber =
                mRepository.getLocationInfo(queryType, workId, invId, location)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.checkLocationFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.checkLocationSuccess(batchFlag, location);
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getTransferInfoSingle(final String refCodeId, final String refType, final String bizType,
                                      final String refLineId, final String batchFlag, final String location,
                                      final String userId) {
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
