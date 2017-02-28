package com.richfit.barcodesystemproduct.module.welcome;

import android.animation.Animator;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.common_lib.utils.AppCompat;
import com.richfit.common_lib.utils.GUIUtils;
import com.richfit.common_lib.utils.Global;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * Created by monday on 2016/11/8.
 */

public class WelcomeActivity extends BaseActivity<WelcomePresenterImp> implements WelcomeContract.View {

    @BindView(R.id.btn_online_mode)
    Button btnOnlineMode;

    @BindView(R.id.btn_offline_mode)
    Button btnOfflineMode;

    @BindView(R.id.reveal_view)
    View revealView;

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
        final View view = Global.ONLINE_MODE == mode ? btnOnlineMode : btnOfflineMode;
        int primaryColor = AppCompat.getColor(R.color.colorPrimary,this);
        int[] location = new int[2];
        revealView.setBackgroundColor(primaryColor);
        view.getLocationOnScreen(location);
        int cx = (location[0] + (view.getWidth() / 2));
        int cy = location[1] + (GUIUtils.getStatusBarHeight(this) / 2);
        hideNavigationStatus();
        GUIUtils.showRevealEffect(revealView, cx, cy, new RevealAnimationListener(mode));

    }

    @Override
    public void loadConfigFail(String message) {
        showMessage(message);
    }

    private void hideNavigationStatus() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected class RevealAnimationListener implements Animator.AnimatorListener {

        private final int mode;

        public RevealAnimationListener(int mode) {
            this.mode = mode;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mPresenter.toHome(mode);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }


    @Override
    public void networkConnectError(String retryAction) {

    }
}
