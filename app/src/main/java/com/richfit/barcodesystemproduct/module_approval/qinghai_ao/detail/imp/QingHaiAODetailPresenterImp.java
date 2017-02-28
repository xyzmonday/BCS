package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail.IQingHaiAODetailPresenter;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail.IQingHaiAODetailView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/2/28.
 */

public class QingHaiAODetailPresenterImp extends BasePresenter<IQingHaiAODetailView>
        implements IQingHaiAODetailPresenter {

    IQingHaiAODetailView mView;

    @Inject
    public QingHaiAODetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getReference(ReferenceEntity data, String refNum, String refType, String bizType, String moveType, String userId) {

        mView = getView();

        ResourceSubscriber<ReferenceEntity> subscriber = mRepository.getReference(refNum, refType, bizType, moveType, userId)
                .filter(refData -> refData != null && refData.billDetailList != null && refData.billDetailList.size() > 0)
                .map(refData -> addTreeInfo(refData))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<ReferenceEntity>() {
                    @Override
                    public void onNext(ReferenceEntity refData) {
                        if (mView != null) {
                            mView.showNodes(refData.billDetailList);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.setRefreshing(false, "获取明细失败" + t.getMessage());
                            //展示抬头获取的数据，没有缓存
                            mView.showNodes(data.billDetailList);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.setRefreshing(true, "获取明细成功");
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void deleteNode(String lineDeleteFlag, String refNum, String refLineNum, String refLineId, String refType, String bizType, String userId, int position, String companyCode) {
        mView = getView();

        RxSubscriber<String> subscriber =
                mRepository.deleteCollectionDataSingle("", "", "", "", refType, bizType, refLineId,
                        userId, position, companyCode)
                        .doOnNext(str -> mRepository.deleteInspectionImagesSingle(refNum, refLineNum, refLineId, false))
                        .doOnNext(str -> FileUtil.deleteDir(FileUtil.getImageCacheDir(mContext.getApplicationContext(),refNum, refLineNum,false)))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<String>(mContext) {
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
        addSubscriber(subscriber);
    }

    @Override
    public void editNode(RefDetailEntity node, String companyCode, String bizType, String refType, String subFunName, int position) {

    }

    @Override
    public void transferCollectionData(String transId, String bizType, String refType, String userId, String voucherDate, Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mView = getView();

        RxSubscriber<String> subscriber = mRepository.transferCollectionData(transId, bizType, refType, userId, voucherDate,
                flagMap, extraHeaderMap)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在上传数据...") {
                    @Override
                    public void _onNext(String message) {
                        if (mView != null) {
                            mView.showTransNum(message);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.submitDataFail(message);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.submitDataFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.submitDataFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.showSubmitComplete();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void showHeadFragmentByPosition(int position) {

    }
}
