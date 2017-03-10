package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail.imp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module.main.MainActivity;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail.IApprovalOtherDetailPresenter;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail.IApprovalOtherDetailView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ImageEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/24.
 */

public class ApprovalOtherDetailPresenterImp extends BasePresenter<IApprovalOtherDetailView>
        implements IApprovalOtherDetailPresenter {

    IApprovalOtherDetailView mView;
    private String mSuccessMessage;

    @Inject
    public ApprovalOtherDetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getReference(ReferenceEntity data, @NonNull String refNum, @NonNull String refType, @NonNull String bizType,
                             @NonNull String moveType, String refLineId, @NonNull String userId) {
        mView = getView();

        ResourceSubscriber<ReferenceEntity> subscriber =
                mRepository.getReference(refNum, refType, bizType, moveType, refLineId, userId)
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
    public void deleteNode(String lineDeleteFlag, String refNum, String refLineNum, String refLineId,
                           String refType, String bizType, String userId, int position, String companyCode) {
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
    public void editNode(RefDetailEntity node, String companyCode, String bizType,
                         String refType, String subFunName, int position) {
        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle bundle = new Bundle();
        //该子节点的id
        bundle.putString(Global.EXTRA_REF_LINE_ID_KEY, node.refLineId);
        //入库子菜单类型
        bundle.putString(Global.EXTRA_COMPANY_CODE_KEY, companyCode);
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);

        //父节点的位置
        bundle.putInt(Global.EXTRA_POSITION_KEY, position);
        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, subFunName);

        //库存地点Id
        bundle.putString(Global.EXTRA_INV_ID_KEY, node.invId);
        bundle.putString(Global.EXTRA_INV_CODE_KEY, node.invCode);

        //缓存
        bundle.putString(Global.EXTRA_BATCH_FLAG_KEY, node.batchFlag);
        bundle.putString(Global.EXTRA_TOTAL_QUANTITY_KEY, node.totalQuantity);

        //额外字段的数据
        bundle.putSerializable(Global.COLLECT_EXTRA_MAP_KEY, (Serializable) node.mapExt);

        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void uploadCollectionData(String refNum, String refCodeId, String bizType, String refType,
                                     int inspectionType, String voucherDate,
                                     String userId, boolean isLocal) {
        mView = getView();
        if ("0".equals(refType)) {
            addSubscriber(Flowable.concat(uploadInspectedImages(refNum, refCodeId, userId, isLocal),
                    uploadCollectionData(refCodeId, bizType, refType, inspectionType, voucherDate, userId))
                    .doOnComplete(() -> mRepository.deleteInspectionImages(refNum, refCodeId, false))
                    .doOnComplete(() -> FileUtil.deleteDir(FileUtil.getImageCacheDir(mContext.getApplicationContext(), refNum, false)))
                    .compose(TransformerHelper.io2main())
                    .subscribeWith(new RxSubscriber<String>(mContext, "正在过账...") {

                        @Override
                        public void _onNext(String s) {
                            mSuccessMessage = s;
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
                                mView.showSubmitSuccessMessage(mSuccessMessage);
                            }
                        }
                    }));
        } else if ("1".equals(refType)) {
            addSubscriber(readImagesFromLocalAndUpload(refNum, refCodeId, userId, isLocal)
                    .doOnComplete(() -> mRepository.deleteInspectionImages(refNum, refCodeId, false))
                    .doOnComplete(() -> FileUtil.deleteDir(FileUtil.getImageCacheDir(mContext.getApplicationContext(), refNum, false)))
                    .compose(TransformerHelper.io2main())
                    .subscribeWith(new ResourceSubscriber<String>() {

                        @Override
                        public void onNext(String s) {

                        }

                        @Override
                        public void onError(Throwable t) {
                            if (mView != null) {
                                mView.submitDataFail(t.getMessage());
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mView != null) {
                                mView.showSubmitSuccessMessage("图片上传成功!");
                            }
                        }
                    }));
        }
    }

    @Override
    public void showHeadFragmentByPosition(int position) {
        if (mSimpleRxBus.hasSubscribers()) {
            mSimpleRxBus.post(true);
        }
        if (MainActivity.class.isInstance(mContext)) {
            MainActivity activity = (MainActivity) mContext;
            activity.showFragmentByPosition(position);
        }
    }


    private Flowable<String> readImagesFromLocalAndUpload(final String refNum, String refCodeId,
                                                          String userId, final boolean isLocal) {
        return Flowable.create((FlowableOnSubscribe<ArrayList<ImageEntity>>) emitter -> {
            ArrayList<ImageEntity> images = mRepository.readImagesByRefNum(refNum, isLocal);
            if (images == null || images.size() == 0) {
                emitter.onError(new Throwable("请先拍照"));
                return;
            }
            emitter.onNext(images);
            emitter.onComplete();
        }, BackpressureStrategy.LATEST)
                .flatMap(images -> Flowable.fromIterable(images))
                .map(image -> wrapperImage(image, refCodeId, userId))
                .flatMap(result -> mRepository.uploadInspectionImage(result));
    }

    /**
     * 上传图片。如果没有图片，那么直接调用onComplete方法
     */
    private Flowable<String> uploadInspectedImages(String refNum, String refCodeId, String userId, boolean isLocal) {
        return Flowable.just(refNum)
                .flatMap(num -> Flowable.fromIterable(mRepository.readImagesByRefNum(refNum, isLocal)))
                .map(image -> wrapperImage(image, refCodeId, userId))
                .flatMap(result -> mRepository.uploadInspectionImage(result));
    }

    /**
     * 图片上传。将imageEntity装换成上传ResultEntity实体类
     *
     * @return
     */
    private ResultEntity wrapperImage(ImageEntity image, String refCodeId, String userId) {
        ResultEntity result = new ResultEntity();
        result.suffix = Global.IMAGE_DEFAULT_FORMAT;
        result.bizHeadId = refCodeId;
        result.bizLineId = image.refLineId;
        result.imageName = image.imageName;
        result.refType = image.refType;
        result.businessType = image.bizType;
        result.bizPart = "1";
        result.imagePath = image.imageDir + File.separator + result.imageName;
        result.createdBy = image.createBy;
        result.imageDate = image.createDate;
        result.userId = userId;
        result.fileType = image.takePhotoType;
        return result;
    }


    /**
     * 上传整单验收单据的数据。这里需要确认：如果一条都没有验收，那么是什么结果？
     */
    private Flowable<String> uploadCollectionData(String refCodeId, String bizType, String refType,
                                                  int inspectionType, String voucherDate, String userId) {
        return mRepository.uploadCollectionData(refCodeId, "", bizType, refType, inspectionType, voucherDate, "", userId);
    }
}
