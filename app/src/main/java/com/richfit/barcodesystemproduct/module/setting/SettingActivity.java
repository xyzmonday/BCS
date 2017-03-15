package com.richfit.barcodesystemproduct.module.setting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.barcodesystemproduct.module.setting.imp.SettingPresenterImp;
import com.richfit.common_lib.dialog.ProgressDialogFragment;
import com.richfit.common_lib.rxutils.RxCilck;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.ButtonCircleProgressBar;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.UpdateEntity;

import java.util.ArrayList;

import butterknife.BindView;
import ch.ielse.view.SwitchView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by monday on 2016/11/29.
 */

public class SettingActivity extends BaseActivity<SettingPresenterImp>
        implements ISettingView {

    private static final String PROGRESS_DIALOG_TAG = "progress_dialog";

    private static final int LOAD_STATUS_START = 0;
    private static final int LOAD_STATUS_LADING = 1;
    private static final int LOAD_STATUS_PAUSE = 2;
    private static final int LOAD_STATUS_RESUME = 3;
    private static final int LOAD_STATUS_END = 4;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.sb_supplier)
    SwitchView sbSupplier;
    @BindView(R.id.sb_cost_center)
    SwitchView sbCostCenter;
//    @BindView(R.id.sb_warehouse)
//    SwitchView sbWarehouse;
    @BindView(R.id.sb_project_num)
    SwitchView sbProjectNum;
    @BindView(R.id.check_update_apk)
    TextView mCheckUpdateApk;
    @BindView(R.id.reset_password)
    LinearLayout mResetPwd;
    @BindView(R.id.floating_button)
    FloatingActionButton fabButton;
    @BindView(R.id.btn_circle_progress)
    ButtonCircleProgressBar mProgressBar;

    private UpdateEntity mUpdateInfo;
    private int mCurrentLoadStatus = LOAD_STATUS_START;
    private ProgressDialogFragment mProgressDialog;
    private String mMessage;

    @Override
    protected int getContentId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    protected void initViews() {
        setupToolBar();
    }

    @Override
    public void initEvent() {
        /*获取版本信息*/
        RxCilck.clicks(mCheckUpdateApk)
                .filter(a -> mCurrentLoadStatus != LOAD_STATUS_LADING
                        && mCurrentLoadStatus != LOAD_STATUS_PAUSE)
                .subscribe(a -> mPresenter.getAppVersion());

        /*下载基础数据*/
        RxCilck.clicks(fabButton)
                .filter(a -> mCurrentLoadStatus != LOAD_STATUS_LADING)
                .subscribe(a -> startLoadBasicData());

        RxCilck.clicks(mProgressBar)
                .subscribe(a -> {
                    if (mProgressBar.getStatus() == ButtonCircleProgressBar.Status.Starting) {
                        //如果正在下载，那么暂停
                        mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
                        pause();
                    } else {
                        mProgressBar.setStatus(ButtonCircleProgressBar.Status.Starting);
                        if (mUpdateInfo != null)
                            resume(mUpdateInfo.appDownloadUrl, mUpdateInfo.appName);
                    }
                });
        //重置密码
//        RxCilck.clicks(mResetPwd)
//                .subscribe(a->mPresenter.)
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String currentVersionName = UiUtil.getCurrentVersionName(this.getApplicationContext());
        mCheckUpdateApk.setText(mCheckUpdateApk.getText().toString() + "(" + currentVersionName + ")");
    }

    private void startLoadBasicData() {
        ArrayList<LoadBasicDataWrapper> requestParams = new ArrayList<>();
        LoadBasicDataWrapper task = new LoadBasicDataWrapper();
        mMessage = "";
        if (sbSupplier.isOpened()) {

            task.isByPage = true;
            task.queryType = "GYS";
            requestParams.add(task);
            mMessage += "供应商;";
        }

        if(sbCostCenter.isOpened()) {
            task = new LoadBasicDataWrapper();
            task.isByPage = true;
            task.queryType = "CZ";
            requestParams.add(task);
            mMessage += "成本中心;";
        }

        if(sbProjectNum.isOpened()) {
            task = new LoadBasicDataWrapper();
            task.isByPage = true;
            task.queryType = "XM";
            requestParams.add(task);
            mMessage += "项目编号;";
        }

        if (TextUtils.isEmpty(mMessage)) {
            showMessage("请您先选择要下载的数据");
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("您选择将要下载的基础数据包括:\n" + mMessage + "\n按下确定将开始下载数据!");
        dialog.setPositiveButton("确定", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            mPresenter.loadAndSaveBasicData(requestParams);
        });
        dialog.setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }

    private void setupToolBar() {
        //设置标题必须在setSupportActionBar之前才有效
        mToolbarTitle.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        mToolbarTitle.setText("用户设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void checkAppVersion(UpdateEntity info) {
        //获取当前的版本号
        mUpdateInfo = info;
        String currentVersion = UiUtil.getCurrentVersionName(this.getApplicationContext());
        if (UiUtil.convertToFloat(info.appVersion, 0.0f) > UiUtil.convertToFloat(currentVersion, 0.0f)) {
            //提示用户需要更新
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("检测到最新的版本:" + info.appVersion);
            dialog.setMessage(info.appUpdateDesc);
            dialog.setPositiveButton("现在更新", (dialogInterface, i) -> start(info.appDownloadUrl, info.appName));
            dialog.setNegativeButton("以后再说", (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
        }
    }

    @Override
    public void getUpdateInfoFail(String message) {
        showMessage(message);
    }

    @Override
    public void prepareLoadApp() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setStatus(ButtonCircleProgressBar.Status.Starting);
    }

    @Override
    public void loadLatestAppFail(String message) {
        showMessage(message);
        mCurrentLoadStatus = LOAD_STATUS_START;
        mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
    }

    @Override
    public void showLoadProgress(DownloadStatus status) {
        mCurrentLoadStatus = LOAD_STATUS_LADING;
        mProgressBar.setMax((int) status.getTotalSize());
        mProgressBar.setProgress((int) status.getDownloadSize());
    }

    @Override
    public void loadComplete() {
        mCurrentLoadStatus = LOAD_STATUS_END;
        mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private void start(String url, String saveName) {
        mCurrentLoadStatus = LOAD_STATUS_START;
        String apkCacheDir = FileUtil.getApkCacheDir(this.getApplicationContext());
        mPresenter.loadLatestApp(url, saveName, apkCacheDir);
    }

    private void resume(String url, String saveName) {
        mCurrentLoadStatus = LOAD_STATUS_RESUME;
        String apkCacheDir = FileUtil.getApkCacheDir(this.getApplicationContext());
        mPresenter.loadLatestApp(url, saveName, apkCacheDir);
    }

    private void pause() {
        mCurrentLoadStatus = LOAD_STATUS_PAUSE;
        mPresenter.pauseLoadApp();
    }

    @Override
    public void onStartLoadBasicData(int maxProgress) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mProgressDialog = (ProgressDialogFragment) fragmentManager.findFragmentByTag(PROGRESS_DIALOG_TAG);
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialogFragment();
        }
        mProgressDialog.setMaxProgress(maxProgress);
        if (!mProgressDialog.isAdded())
            mProgressDialog.show(fragmentManager, PROGRESS_DIALOG_TAG);
    }

    @Override
    public void loadBasicDataProgress(float progress) {
        L.e("下载进度 = " + progress);
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void loadBasicDataFail(String message) {
        showMessage(message);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void loadBasicDataSuccess() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("下载成功")
                .setContentText(mMessage + "基础数据下载成功,请进行其他的操作")
                .show();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}


