package com.richfit.barcodesystemproduct.module_acceptstore.ww_component;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.richfit.barcodesystemproduct.adapter.MainPagerViewAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module.main.MainPresenterImp;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/3/10.
 */

public class DSWWCComponentPresenterImp extends MainPresenterImp {

    @Inject
    public DSWWCComponentPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void setupMainContent(final FragmentManager fragmentManager, String companyCode,
                                 String moduleCode, String bizType, String refType,
                                 String lineNum, int currentPageIndex) {

        mView = getView();
        ResourceSubscriber<MainPagerViewAdapter> subscriber =
                mRepository.readBizFragmentConfig(bizType, refType, 1)
                        .filter(bizFragmentConfigs -> bizFragmentConfigs != null && bizFragmentConfigs.size() > 0)
                        .flatMap(bizFragmentConfigs -> Flowable.fromIterable(bizFragmentConfigs))
                        .map(config -> {
                            Bundle bundle = new Bundle();
                            bundle.putString(Global.EXTRA_REF_LINE_NUM_KEY, lineNum);
                            bundle.putString(Global.EXTRA_COMPANY_CODE_KEY, companyCode);
                            bundle.putString(Global.EXTRA_MODULE_CODE_KEY, moduleCode);
                            bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, config.bizType);
                            bundle.putString(Global.EXTRA_REF_TYPE_KEY, config.refType);
                            bundle.putString(Global.EXTRA_TITLE_KEY, config.tabTitle);
                            bundle.putInt(Global.EXTRA_FRAGMENT_TYPE_KEY, config.fragmentType);
                            return BaseFragment.findFragment(fragmentManager, config.fragmentTag, bundle, config.className);
                        })
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
