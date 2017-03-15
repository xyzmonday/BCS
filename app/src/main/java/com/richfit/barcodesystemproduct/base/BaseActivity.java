package com.richfit.barcodesystemproduct.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.StatusBarCompat;
import com.richfit.common_lib.utils.ViewServer;
import com.richfit.domain.bean.RowConfig;

import java.io.ByteArrayOutputStream;
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

        //注册接收扫描结构的结果的广播
        IntentFilter scanDataIntentFilter = new IntentFilter();
        scanDataIntentFilter.addAction(WZ_RECT_DATA_ACTION);
        scanDataIntentFilter.addAction(DQ_RECE_DATA_ACTION);
        registerReceiver(receiver, scanDataIntentFilter);

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
        //注意这里如果不在onDestroy方法里面不释放当前Activity的实例，那么将出现内存泄露
//        AppManager.addActivity(this);
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
        unregisterReceiver(receiver);
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
     *
     * @param action
     */
    @Override
    public void retry(String action) {
        closeNetConnectErrorDialog();
    }

    /**
     * 网络发生异常时，回调该方法，子类可以重写该方法进行处理改异常
     *
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


    /**
     * 扫描条码模块
     */
    public static final int KEY_SCAN = 135;
    //最中间的红色扫描按键
    public static final int KEY_F1 = 134;
    //2016-08-02新的手持适配，ARMOR手持，android 5.1.1系统
    public static final int KEY_SCAN1 = 0;
    /**
     * 大庆手持的扫码广播
     */
    private static final String DQ_RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private static final String DQ_START_SCAN_ACTION = "com.geomobile.se4500barcode";
    private static final String DQ_STOP_SCAN = "com.geomobile.se4500barcode.poweroff";
    /**
     * 物资公司的扫码广播
     */
    private static final String WZ_RECT_DATA_ACTION = "com.android.scancontext";
    private static final String WZ_START_SCAN_ACTION = "android.intent.action.FUNCTION_BUTTON_DOWN";
    private static final String WZ_STOP_SCAN = "android.intent.action.FUNCTION_BUTTON_UP";

    private boolean isStartScan = false;
    public static String mType;

    /**
     * 扫描条码
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEY_SCAN:
                startScan();
                break;
            case KEY_F1:
                startScan();
                break;
            case KEY_SCAN1:
                startScan();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 停止扫描
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEY_SCAN:
                stopScan();
                break;
            case KEY_F1:
                stopScan();
                break;
            case KEY_SCAN1:
                stopScan();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 发送广播开始调用系统扫描
     */
    private void startScan() {
        if (!isStartScan) {
            isStartScan = true;
            //如果没有开启
            Intent intent = new Intent();
            intent.setAction(WZ_START_SCAN_ACTION);
            intent.setAction(DQ_START_SCAN_ACTION);
            sendBroadcast(intent, null);
        }
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        isStartScan = false;
        Intent intent = new Intent();
        intent.setAction(WZ_STOP_SCAN);
        intent.setAction(DQ_STOP_SCAN);
        sendBroadcast(intent);

    }

    /**
     * 接收扫描信息的广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isStartScan = true;
            String action = intent.getAction();
            if (action.equals("com.android.scancontext")) {
                // “前台输出”不打勾时，不会发送此Intent
                String data = intent.getStringExtra("Scan_context");
                filerBarCodeInfo(data);
            } else if (action.equals(WZ_RECT_DATA_ACTION)) {
                String data = intent.getStringExtra("Scan_context");
                filerBarCodeInfo(data);
            } else if (action.equals(DQ_RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                filerBarCodeInfo(data);
            }
        }
    };

    /**
     * 更具条码的类型，获取条码的信息
     * 条码类型|物料编码|批次|验收人|采购订单号
     */
    private void filerBarCodeInfo(String info) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if(!isStartScan)
            return;
        final String barcodeInfo = CharTrans(info);
        L.e("扫描到的原始单据信息 = " + barcodeInfo);
        String a[] = barcodeInfo.split("\\|", -1);
        handleBarCodeScanResult(mType, a);
    }
    /**
     * 条码内容加密
     * @param char_in
     * @return
     */
    public static String CharTrans(String char_in) {
        String char_out = "";
        int char_length = 0;

        char_length = char_in.length();

        int flg_mod = char_length % 2;
        for (int i = 0; i < char_length - 1; i += 2) {
            char_out = char_out + char_in.substring(i + 1, i + 2);
            char_out = char_out + char_in.substring(i, i + 1);
        }

        if (flg_mod != 0) {
            char_out = char_out + char_in.substring(char_length - 1);
        }
        return char_out;
    }


    /**
     * 16进制数字字符集
     */
    private static final String hexString = "0123456789ABCDEF";

    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decodeForChinese(String bytes) {
        String str = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        try {
            str = new String(baos.toByteArray(), "GB2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    //处理条码扫描
    protected void handleBarCodeScanResult(String type, String[] list) {

    }

}
