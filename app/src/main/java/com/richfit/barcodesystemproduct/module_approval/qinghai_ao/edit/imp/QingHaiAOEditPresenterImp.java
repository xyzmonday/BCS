package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.edit.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.edit.IQingHaiAOEditPresenter;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.edit.IQingHaiAOEditView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/3/1.
 */

public class QingHaiAOEditPresenterImp extends BasePresenter<IQingHaiAOEditView>
        implements IQingHaiAOEditPresenter {

    IQingHaiAOEditView mView;

    @Inject
    public QingHaiAOEditPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getInvsByWorkId(String workId,int flag) {
        mView = getView();
        if(TextUtils.isEmpty(workId) && mView != null) {
            mView.loadInvsFail("工厂Id为空");
            return;
        }
        ResourceSubscriber<ArrayList<InvEntity>> subscriber =
                mRepository.getInvsByWorkId(workId,flag)
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<ArrayList<InvEntity>>() {
                            @Override
                            public void onNext(ArrayList<InvEntity> invs) {
                                if(mView != null) {
                                    mView.showInvs(invs);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if(mView != null) {
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
    public void uploadInspectionDataSingle(ResultEntity result) {
        mView = getView();

        RxSubscriber<String> subscriber = mRepository.uploadCollectionDataSingle(result)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在保存数据...") {
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
