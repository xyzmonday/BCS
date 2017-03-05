package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header.IASNHeaderPresenter;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header.IASNHeaderView;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.domain.bean.SimpleEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/16.
 */

public class ASNHeaderPresenterImp extends BasePresenter<IASNHeaderView>
        implements IASNHeaderPresenter {

    IASNHeaderView mView;

    @Inject
    public ASNHeaderPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
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
                        if (mView != null) {
                            mView.loadWorksComplete();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getSupplierList(String workCode, String keyWord, int defaultItemNum, int flag) {
        mView = getView();
        ResourceSubscriber<ArrayList<SimpleEntity>> subscriber =
                mRepository.getSupplierList(workCode, keyWord, defaultItemNum, flag)
                        .filter(list -> list != null && list.size() > 0)
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<ArrayList<SimpleEntity>>() {
                            @Override
                            public void onNext(ArrayList<SimpleEntity> list) {
                                if (mView != null) {
                                    mView.showSuppliers(list);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.loadSuppliersFail(t.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }


    @Override
    public void getMoveTypeList(int flag) {
        ResourceSubscriber<ArrayList<String>> subscriber = Flowable.just(10)
                .map(num -> mockMoveTypeList(num))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<ArrayList<String>>() {
                    @Override
                    public void onNext(ArrayList<String> strings) {
                        if (mView != null) {
                            mView.showMoveTypes(strings);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.loadMoveTypesFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        addSubscriber(subscriber);
    }

    private ArrayList<String> mockMoveTypeList(int num) {
        ArrayList<String> list = new ArrayList<>();
        list.add("请选择");
        for (int i = 0; i < num; i++)
            list.add("测试数据,移动类型#" + i);
        return list;
    }

    @Override
    public void deleteCollectionData(String refType, String bizType, String userId, String companyCode) {
        mView = getView();
        RxSubscriber<String> subscriber = mRepository.deleteCollectionData("", "", "", refType, bizType, userId, companyCode)
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
}
