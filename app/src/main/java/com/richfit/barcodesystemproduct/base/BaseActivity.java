package com.richfit.barcodesystemproduct.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.richfit.barcodesystemproduct.SampleApplicationLike;
import com.richfit.barcodesystemproduct.di.component.ActivityComponent;
import com.richfit.barcodesystemproduct.di.component.DaggerActivityComponent;
import com.richfit.barcodesystemproduct.di.module.ActivityModule;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.common_lib.dialog.NetConnectErrorDialogFragment;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.StatusBarCompat;
import com.richfit.common_lib.utils.ViewServer;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by monday on 2016/10/27.
 */

public abstract class BaseActivity<T extends IPresenter> extends AppCompatActivity implements BaseView,
        NetConnectErrorDialogFragment.INetworkConnectListener {

    protected ActivityComponent mActivityComponent;

    @Inject
    protected T mPresenter;

    @BindView(android.R.id.content)
    protected View mView;

    private Unbinder mUnbinder;

    private static Boolean isExit = false;

    private boolean mOpenStatusBar = true;

    protected NetConnectErrorDialogFragment mNetConnectErrorDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(SampleApplicationLike.getAppComponent())
                .build();
        super.onCreate(savedInstanceState);

        int layoutId = getContentId();
        if (layoutId > 0) {
            setContentView(getContentId());
            mUnbinder = ButterKnife.bind(this);
        }
        initInjector();
        if (mPresenter != null)
            mPresenter.attachView(this);
        initVariables();
        initViews();
        initData(savedInstanceState);
        initEvent();
        if (mOpenStatusBar)
            StatusBarCompat.compat(this);
        ViewServer.get(this).addWindow(this);
    }

    protected void setStatusBar(boolean isOpenStatusBar) {
        mOpenStatusBar = isOpenStatusBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetConnectErrorDialogFragment != null) {
            mNetConnectErrorDialogFragment.setINetworkConnectListener(null);
            mNetConnectErrorDialogFragment.dismiss();
        }
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) mUnbinder.unbind();
        if (mPresenter != null)
            //防止内存泄露
            mPresenter.detachView();
        ViewServer.get(this).removeWindow(this);
    }


    public void initVariables() {
    }

    protected void initViews() {
    }


    //初始化必要的数据
    public void initData(Bundle savedInstanceState) {
    }


    //初始化事件监听
    public void initEvent() {
    }


    //视图id
    @LayoutRes
    protected abstract int getContentId();

    /**
     * 注入Injector
     */
    public abstract void initInjector();

    protected List<String> getStringArray(@ArrayRes int id) {
        return Arrays.asList(getResources().getStringArray(id));
    }

    protected void showMessage(String message) {
        if (mView != null) {
            Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void hideKeyboard(View view) {
        if (view == null)
            view = this.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 设置ToolBar和DrawerLayout
     */
    protected void setupToolBar(int toolbarId, int toolBarTitleId, String title) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        //设置标题必须在setSupportActionBar之前才有效
        TextView toolBarTitle = (TextView) toolbar.findViewById(toolBarTitleId);
        toolBarTitle.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolBarTitle.setText(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void exitBy2Click() {
        if (!isExit) {
            isExit = true; // 准备退出
            showMessage("再按一次退出");
            // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
            Flowable.timer(2000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe(aLong -> isExit = false);
        } else {
            finish();
        }
    }

    /**
     * 显示网络错误重试对话框
     *
     * @param action：重试的事件类型
     */
    protected void showNetConnectErrorDialog(String action) {
        mNetConnectErrorDialogFragment = (NetConnectErrorDialogFragment) getSupportFragmentManager().findFragmentByTag("NETWORK_CONNECT_ERROR_TAG");
        if (mNetConnectErrorDialogFragment == null) {
            mNetConnectErrorDialogFragment = NetConnectErrorDialogFragment.newInstance(action);
            mNetConnectErrorDialogFragment.setINetworkConnectListener(this);
        }
        if (!mNetConnectErrorDialogFragment.isAdded())
            mNetConnectErrorDialogFragment.show(getSupportFragmentManager(), Global.NETWORK_CONNECT_ERROR_TAG);
    }

    protected void closeNetConnectErrorDialog() {
        if (mNetConnectErrorDialogFragment != null) {
            mNetConnectErrorDialogFragment.dismiss();
        }
    }

    /**
     * 用户点击重试按钮，回到该方法
     * @param action
     */
    @Override
    public void retry(String action) {
        closeNetConnectErrorDialog();
    }

    /**
     * 网络发生异常时，回调该方法，子类可以重写该方法进行处理改异常
     * @param retryAction：重试的action
     */
    @Override
    public void networkConnectError(String retryAction) {

    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {

    }

    @Override
    public void readConfigsFail(String message) {

    }

    @Override
    public void readConfigsComplete() {

    }

    @Override
    public void readExtraDictionarySuccess(Map<String, Object> datas) {

    }

    @Override
    public void readExtraDictionaryFail(String message) {

    }

    @Override
    public void readExtraDictionaryComplete() {

    }

}
