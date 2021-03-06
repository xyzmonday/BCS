package com.richfit.barcodesystemproduct.module_check.qinghai_cn.edit.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_check.qinghai_cn.edit.ICNEditPresenter;
import com.richfit.barcodesystemproduct.module_check.qinghai_cn.edit.ICNEditView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ResultEntity;

import javax.inject.Inject;

/**
 * Created by monday on 2016/12/6.
 */

public class CNEditPresenterImp extends BasePresenter<ICNEditView>
        implements ICNEditPresenter {

    ICNEditView mView;

    @Inject
    public CNEditPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }


    @Override
    public void uploadCheckDataSingle(ResultEntity result) {
        mView = getView();
        addSubscriber(mRepository.uploadCheckDataSingle(result)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在保存盘点数据...") {
                    @Override
                    public void _onNext(String s) {

                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if(mView != null) {
                            mView.networkConnectError(Global.RETRY_UPLOAD_DATA_ACTION);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if(mView != null) {
                            mView.saveCheckDataFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if(mView != null) {
                            mView.saveCheckDataFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if(mView != null) {
                            mView.saveCheckDataSuccess();
                        }
                    }
                }));
    }
}
