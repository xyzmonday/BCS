package com.richfit.barcodesystemproduct.module_locationadjust.header.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_locationadjust.header.ILAHeaderPresenter;
import com.richfit.barcodesystemproduct.module_locationadjust.header.ILAHeaderView;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/2/7.
 */

public class LAHeaderPresenterImp extends BasePresenter<ILAHeaderView>
        implements ILAHeaderPresenter {

    ILAHeaderView mView;

    @Inject
    public LAHeaderPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }


    @Override
    public void getWorks(int flag) {
        mView = getView();
        ResourceSubscriber<ArrayList<WorkEntity>> subscriber =
                mRepository.getWorks(flag)
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
    public void getInvsByWorkId(String workId,int flag) {
        mView = getView();
        if (TextUtils.isEmpty(workId) && mView != null) {
            mView.loadInvsFail("请先选择工厂");
            return;
        }
        ResourceSubscriber<ArrayList<InvEntity>> subscriber =
                mRepository.getInvsByWorkId(workId,flag)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<ArrayList<InvEntity>>() {
                    @Override
                    public void onNext(ArrayList<InvEntity> invs) {
                        if (mView != null) {
                            mView.showInvs(invs);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
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
    public void getStorageNum(String workId, String workCode, String invId, String invCode) {
        mView = getView();

        if(TextUtils.isEmpty(workId)) {
            mView.getStorageNumFail("工厂id为空");
            return;
        }

        if(TextUtils.isEmpty(invCode)) {
            mView.getStorageNumFail("库存地点为空");
            return;
        }

        mRepository.getStorageNum(workId,workCode,invId,invCode)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        if(mView != null) {
                            mView.getStorageNumSuccess(s);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if(mView != null) {
                            mView.getStorageNumFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
