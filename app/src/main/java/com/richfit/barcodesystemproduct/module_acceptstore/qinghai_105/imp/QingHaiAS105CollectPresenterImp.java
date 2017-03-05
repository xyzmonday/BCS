package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.imp.ASCollectPresenterImp;

import javax.inject.Inject;

/**
 * Created by monday on 2017/3/2.
 */

public class QingHaiAS105CollectPresenterImp extends ASCollectPresenterImp{

    @Inject
    public QingHaiAS105CollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
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
        mView.checkLocationSuccess(batchFlag, location);
//        ResourceSubscriber<String> subscriber =
//                mRepository.getLocationInfo(queryType, workId, invId, location)
//                        .compose(TransformerHelper.io2main())
//                        .subscribeWith(new ResourceSubscriber<String>() {
//                            @Override
//                            public void onNext(String s) {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable t) {
//                                if (mView != null) {
//                                    mView.checkLocationFail(t.getMessage());
//                                }
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                if (mView != null) {
//                                    mView.checkLocationSuccess(batchFlag, location);
//                                }
//                            }
//                        });
//        addSubscriber(subscriber);
    }
}
