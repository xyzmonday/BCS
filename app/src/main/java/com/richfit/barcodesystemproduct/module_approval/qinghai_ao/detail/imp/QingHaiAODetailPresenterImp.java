package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail.imp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail.IQingHaiAODetailPresenter;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.detail.IQingHaiAODetailView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.io.Serializable;
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
                            mView.showNodes(refData.billDetailList,refData.transId);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.setRefreshing(false, "获取明细失败" + t.getMessage());
                            //展示抬头获取的数据，没有缓存
                            mView.showNodes(data.billDetailList,data.transId);
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
                        .doOnNext(str -> FileUtil.deleteDir(FileUtil.getImageCacheDir(mContext.getApplicationContext(), refNum, refLineNum, false)))
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
    public void editNode(RefDetailEntity node, String companyCode, String bizType, String refType,
                         String subFunName, int position) {
        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt(Global.EXTRA_POSITION_KEY, position);

        //入库子菜单类型
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);

        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, subFunName + "-明细修改");
        //库存地点
        bundle.putString(Global.EXTRA_INV_CODE_KEY, node.invCode);
        bundle.putString(Global.EXTRA_INV_ID_KEY, node.invId);

        //制造商
        bundle.putString(Global.EXTRA_MANUFUCTURER_KEY, node.manufacturer);
        //实收数量(注意这里没有仓位级，所以实收数量为totalQuantity)
        bundle.putString(Global.EXTRA_QUANTITY_KEY, node.totalQuantity);
        //抽检数量
        bundle.putString(Global.EXTRA_SAMPLE_QUANTITY_KEY, node.randomQuantity);
        //完好数量
        bundle.putString(Global.EXTRA_QUALIFIED_QUANTITY_KEY, node.qualifiedQuantity);
        //损坏数量
        bundle.putString(Global.EXTRA_DAMAGED_QUANTITY_KEY, node.damagedQuantity);
        //送检数量
        bundle.putString(Global.EXTRA_INSPECTION_QUANTITY_KEY, node.inspectionQuantity);
        //锈蚀数量
        bundle.putString(Global.EXTRA_RUST_QUANTITY_KEY, node.rustQuantity);
        //变质
        bundle.putString(Global.EXTRA_BAD_QUANTITY_KEY, node.badQuantity);
        //其他数量
        bundle.putString(Global.EXTRA_OTHER_QUANTITY_KEY, node.otherQuantity);
        //包装情况
        bundle.putString(Global.EXTRA_PACKAGE_KEY, node.sapPackage);
        //质检单号
        bundle.putString(Global.EXTRA_QM_NUM_KEY, node.qmNum);
        //索赔单号
        bundle.putString(Global.EXTRA_CLAIM_NUM_KEY, node.claimNum);
        //合格证
        bundle.putString(Global.EXTRA_CERTIFICATE_KEY, node.certificate);
        //说明书
        bundle.putString(Global.EXTRA_INSTRUCTIONS_KEY, node.instructions);
        //质检证书
        bundle.putString(Global.EXTRA_QM_CERTIFICATE_KEY, node.qmCertificate);
        //检验结果
        bundle.putString(Global.EXTRA_INSPECTION_RESULT_KEY, node.inspectionResult);
        //额外字段的数据
        bundle.putSerializable(Global.LOCATION_EXTRA_MAP_KEY, (Serializable) node.mapExt);
        intent.putExtras(bundle);
        Activity activity = (Activity) mContext;
        activity.startActivity(intent);
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
