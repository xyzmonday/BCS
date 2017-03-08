package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_acceptstore.basedetail.imp.ASDetailPresenterImp;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * Created by monday on 2017/3/7.
 */

public class QingHaiAS105DetailPresenterImp extends ASDetailPresenterImp {

    @Inject
    public QingHaiAS105DetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void submitData2BarcodeSystem(String transId, String bizType, String refType, String userId, String voucherDate,
                                         Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mView = getView();
        RxSubscriber<String> subscriber = Flowable.concat(mRepository.uploadCollectionData("", transId, bizType, refType, -1, voucherDate, "", userId),
                mRepository.transferCollectionData(transId, bizType, refType, userId, voucherDate, flagMap, extraHeaderMap))
                .doOnError(str -> SPrefUtil.saveData(bizType + refType, "0"))
                .doOnComplete(() -> SPrefUtil.saveData(bizType + refType, "1"))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在过账...") {
                    @Override
                    public void _onNext(String message) {
                        if (mView != null) {
                            mView.showTransferedVisa(message);
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
                            mView.submitBarcodeSystemFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.submitBarcodeSystemFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.submitBarcodeSystemSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void submitData2SAP(String transId, String bizType, String refType, String userId,
                               String voucherDate, Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mView = getView();
        RxSubscriber<String> subscriber = mRepository.transferCollectionData(transId, bizType, refType, Global.USER_ID, voucherDate, flagMap, extraHeaderMap)
                .doOnComplete(() -> SPrefUtil.saveData(bizType + refType, "0"))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在上传数据...") {
                    @Override
                    public void _onNext(String message) {
                        if (mView != null) {
                            mView.showInspectionNum(message);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.networkConnectError(Global.RETRY_UPLOAD_DATA_ACTION);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null && !TextUtils.isEmpty(message)) {
                            mView.submitSAPFail(message.split("_"));
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.submitSAPFail(new String[]{message});
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.submitSAPSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    /**
     * 通过insLot将缓存和原始单据行关联起来
     */
    @Override
    protected RefDetailEntity getLineDataByRefLineId(String insLot, ReferenceEntity refData) {
        if (TextUtils.isEmpty(insLot))
            return null;
        List<RefDetailEntity> detail = refData.billDetailList;
        for (RefDetailEntity entity : detail) {
            if (insLot.equals(entity.insLot)) {
                return entity;
            }
        }
        return null;
    }


}
