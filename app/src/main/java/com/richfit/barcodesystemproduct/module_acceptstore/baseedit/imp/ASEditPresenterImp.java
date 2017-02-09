package com.richfit.barcodesystemproduct.module_acceptstore.baseedit.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_acceptstore.baseedit.IASEditPresenter;
import com.richfit.barcodesystemproduct.module_acceptstore.baseedit.IASEditView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ResultEntity;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/19.
 */

public class ASEditPresenterImp extends BasePresenter<IASEditView>
        implements IASEditPresenter {

    IASEditView mView;

    @Inject
    public ASEditPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void uploadCollectionDataSingle(ResultEntity result) {
        mView = getView();
        ResourceSubscriber<String> subscriber =
                Flowable.concat(mRepository.getLocationInfo("04", result.workId, result.invId, result.location),
                        mRepository.uploadCollectionDataSingle(result))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<String>(mContext) {
                            @Override
                            public void _onNext(String s) {

                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.networkConnectError(Global.RETRY_EDIT_DATA_ACTION);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if (mView != null) {
                                    mView.saveEditedDataFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.saveEditedDataFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {
                                if (mView != null) {
                                    mView.saveEditedDataSuccess("修改成功");
                                }
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

        ResourceSubscriber<String> subscriber = mRepository.getLocationInfo(queryType, workId, invId, location)
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
                            mView.checkLocationSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

}
