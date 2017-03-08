package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_movestore.basedetail_n.imp.NMSDetailPresenterImp;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;

import javax.inject.Inject;

/**
 * Created by monday on 2017/2/15.
 */

public class QingYangNMS301DetailPresenterImp extends NMSDetailPresenterImp {

    @Inject
    public QingYangNMS301DetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    public void submitData2BarcodeSystem(String transId, String bizType, String refType, String voucherDate) {
        mView = getView();
        RxSubscriber<String> subscriber =
                mRepository.uploadCollectionData("", transId, bizType, refType, -1, voucherDate, "", "")
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<String>(mContext) {
                            @Override
                            public void _onNext(String s) {
                                if (mView != null) {
                                    mView.showTransferedVisa(s);
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
}
