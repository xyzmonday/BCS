package com.richfit.barcodesystemproduct.module.edit;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.domain.bean.BizFragmentConfig;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/1/13.
 */

public class EditPresenterImp extends BasePresenter<IEditContract.View>
        implements IEditContract.Presenter {

    IEditContract.View mView;

    @Inject
    public EditPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }


    @Override
    public void setupEditFragment(String bizType, String refType, int fragmentType) {
        mView = getView();

        if (TextUtils.isEmpty(bizType)) {
            mView.initEditFragmentFail("业务类型为空");
            return;
        }
        if (fragmentType > 0) {
            mView.initEditFragmentFail("fragmentType有误");
            return;
        }

        addSubscriber(mRepository.readBizFragmentConfig(bizType, refType, fragmentType)
                .filter(bizFragmentConfigs -> bizFragmentConfigs != null && bizFragmentConfigs.size() > 0)
                .flatMap(bizFragmentConfigs -> Flowable.fromIterable(bizFragmentConfigs))
                .take(1)
                .filter(fragment -> fragment != null)
                .subscribeWith(new ResourceSubscriber<BizFragmentConfig>() {
                    @Override
                    public void onNext(BizFragmentConfig config) {
                        if (mView != null) {
                            mView.showEditFragment(config);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.initEditFragmentFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }
}
