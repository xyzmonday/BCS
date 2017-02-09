package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.edit.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.edit.IApprovalOtherEditPresenter;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.edit.IApprovalOtherEditView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ResultEntity;

import javax.inject.Inject;

/**
 * Created by monday on 2016/11/29.
 */

public class ApprovalOtherEditPresenterImp extends BasePresenter<IApprovalOtherEditView>
        implements IApprovalOtherEditPresenter {

    IApprovalOtherEditView mView;

    @Inject
    public ApprovalOtherEditPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void uploadInspectionDataSingle(ResultEntity result) {
        mView = getView();

        RxSubscriber<String> subscriber = mRepository.uploadCollectionDataSingle(result)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在修改...") {
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
