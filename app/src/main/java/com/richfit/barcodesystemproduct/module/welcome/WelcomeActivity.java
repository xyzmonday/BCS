package com.richfit.barcodesystemproduct.module.welcome;

import com.jakewharton.rxbinding.view.RxView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.widget.FButton;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * Created by monday on 2016/11/8.
 */

public class WelcomeActivity extends BaseActivity<WelcomePresenterImp> implements WelcomeContract.View {


    @BindView(R.id.btn_online_mode)
    FButton btnOnlineMode;

    @BindView(R.id.btn_offline_mode)
    FButton btnOfflineMode;

    @Override
    protected int getContentId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initEvent() {

        RxView.clicks(btnOnlineMode)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(a -> mPresenter.loadConfig(Global.companyId, Global.ONLINE_MODE));

        RxView.clicks(btnOfflineMode)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(a -> mPresenter.loadConfig(Global.companyId, Global.OFFLINE_MODE));
    }

    @Override
    public void loadConfigSuccess(int mode) {
        showMessage("配置文件导入成功");
        mPresenter.toHome(mode);
    }

    @Override
    public void loadConfigFail(String message) {
        showMessage(message);
    }

    @Override
    public void networkConnectError(String retryAction) {

    }
}
