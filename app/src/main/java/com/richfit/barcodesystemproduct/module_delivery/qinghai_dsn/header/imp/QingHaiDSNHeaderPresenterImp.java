package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.header.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.header.IQingHaiDSNHeaderPresenter;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.header.IQingHaiDSNHeaderView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.domain.bean.CostCenterEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/2/23.
 */

public class QingHaiDSNHeaderPresenterImp extends BasePresenter<IQingHaiDSNHeaderView>
        implements IQingHaiDSNHeaderPresenter {

    protected IQingHaiDSNHeaderView mView;

    @Inject
    public QingHaiDSNHeaderPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void deleteCollectionData(String refType,String bizType, String userId,
                                     String companyCode) {
        mView = getView();
        RxSubscriber<String> subscriber = mRepository.deleteCollectionData("","","",refType,bizType,
                userId,companyCode)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在删除缓存...") {
                    @Override
                    public void _onNext(String message) {
                        if (mView != null) {
                            mView.deleteCacheSuccess(message);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {

                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.deleteCacheFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.deleteCacheFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {

                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getWorks(int flag) {
        mView = getView();
        ResourceSubscriber<ArrayList<WorkEntity>> subscriber = mRepository.getWorks(flag)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<ArrayList<WorkEntity>>() {
                    @Override
                    public void onNext(ArrayList<WorkEntity> works) {
                        if (mView != null) {
                            mView.showWorks(works);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.loadWorksFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getCostCenterList(String workCode, String keyWord, int defaultItemNum, int flag) {
        mView = getView();
        ResourceSubscriber<ArrayList<String>> subscriber =
                mRepository.getCostCenterList(workCode, keyWord, defaultItemNum, flag)
                        .filter(list -> list != null && list.size() > 0)
                        .map(list->wrapper2Str(list))
                        .subscribeWith(new ResourceSubscriber<ArrayList<String>>() {
                            @Override
                            public void onNext(ArrayList<String> suppliers) {
                                if (mView != null) {
                                    mView.showCostCenterList(suppliers);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.loadCostCenterFail(t.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }

    private ArrayList<String> wrapper2Str(ArrayList<CostCenterEntity> list) {
        ArrayList<String> strs = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (CostCenterEntity entity : list) {
            sb.setLength(0);
            sb.append(entity.costCenterCode).append("_").append(entity.costCenterDesc);
            strs.add(sb.toString());
        }
        return strs;
    }
}
