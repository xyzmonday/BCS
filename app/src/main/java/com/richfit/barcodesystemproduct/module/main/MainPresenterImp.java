package com.richfit.barcodesystemproduct.module.main;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.richfit.barcodesystemproduct.adapter.MainPagerViewAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/10.
 */

public class MainPresenterImp extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    MainContract.View mView;

    @Inject
    public MainPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void setupMainContent(final FragmentManager fragmentManager, String companyCode,
                                 String moduleCode, String bizType, String refType, int currentPageIndex) {

        mView = getView();
        ResourceSubscriber<MainPagerViewAdapter> subscriber =
                mRepository.readBizFragmentConfig(bizType, refType, 1)
                        .filter(bizFragmentConfigs -> bizFragmentConfigs != null && bizFragmentConfigs.size() > 0)
                        .flatMap(bizFragmentConfigs -> Flowable.fromIterable(bizFragmentConfigs))
                        .map(config -> BaseFragment.findFragment(fragmentManager,
                                config.fragmentTag, companyCode, moduleCode, bizType, refType,
                                config.fragmentType,
                                config.tabTitle,
                                config.className))
                        .buffer(3)
                        .map(fragments -> {
                            MainPagerViewAdapter adapter = new MainPagerViewAdapter(fragmentManager, fragments);
                            return adapter;
                        })
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<MainPagerViewAdapter>(mContext, "正在初始化页面...") {
                            @Override
                            public void _onNext(MainPagerViewAdapter adapter) {
                                if (mView != null) {
                                    mView.showMainContent(adapter, currentPageIndex);
                                }
                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.setupMainContentFail(message);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if (mView != null) {
                                    mView.setupMainContentFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.setupMainContentFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }
}
