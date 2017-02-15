package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_movestore.basecollect_n.imp.NMSCollectPresenterImp;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ResultEntity;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/2/15.
 */

public class QingYangNMS301CollectPresenterImp extends NMSCollectPresenterImp {

    @Inject
    public QingYangNMS301CollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    /**
     * 重写保存单条数据的接口，因为庆阳的移库不进行仓位检查
     *
     * @param result
     */
    @Override
    public void uploadCollectionDataSingle(ResultEntity result) {
        mView = getView();
        //注意这里需要检查接收仓位是否存在
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
