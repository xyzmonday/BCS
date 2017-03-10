package com.richfit.barcodesystemproduct.module.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BizFragmentConfig;

import butterknife.OnClick;

/**
 * 修改页面基类
 * Created by monday on 2016/11/19.
 */

public class EditActivity extends BaseActivity<EditPresenterImp> implements IEditContract.View {

    protected FragmentManager mFragmentManager;
    protected BaseFragment mFragment = null;
    protected String mTitle;
    protected String mCompanyCode;
    protected String mBizType;
    protected String mRefType;

    @Override
    protected int getContentId() {
        return R.layout.activity_edit;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initVariables() {
        mFragment = null;
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mTitle = bundle.getString(Global.EXTRA_TITLE_KEY);
                mCompanyCode = bundle.getString(Global.EXTRA_COMPANY_CODE_KEY);
                mBizType = bundle.getString(Global.EXTRA_BIZ_TYPE_KEY);
                mRefType = bundle.getString(Global.EXTRA_REF_TYPE_KEY);
            }
        }
    }

    @Override
    public void initViews() {
        setupToolBar(R.id.toolbar, R.id.toolbar_title, mTitle);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mPresenter.setupEditFragment(mBizType, mRefType, getResources().getInteger(R.integer.edit_fragment_type));
    }

    @OnClick(R.id.floating_button)
    public void onClick(View v) {
        //注意这里由于子菜单的code都是从0开始的，所以拍照需要特别设置
        switch (mBizType) {
            default:
                String message = "您真的需要修改数据吗?点击确定将完成修改.";
                showWarningDialog(message);
                break;
        }
    }

    /**
     * 显示提示信息
     *
     * @param message:提示信息
     */
    private void showWarningDialog(String message) {
        Builder builder = new Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确定", (dialog, which) -> {
            /*这里使用了数据采集的状态*/
            if (mFragment != null && mFragment.checkCollectedDataBeforeSave()) {
                mFragment.saveCollectedData();
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showEditFragment(BizFragmentConfig fragmentConfig) {
        final String fragmentTag = fragmentConfig.fragmentTag;
        final String className = fragmentConfig.className;
        if (TextUtils.isEmpty(fragmentTag) || TextUtils.isEmpty(className)) {
            showMessage("不能初始化修改界面");
            return;
        }
        mFragment = BaseFragment.findFragment(mFragmentManager, fragmentTag, getIntent().getExtras(), className);
        if (mFragment != null)
            mFragmentManager.beginTransaction().replace(R.id.edit_content, mFragment, fragmentTag).commit();
    }

    @Override
    public void initEditFragmentFail(String message) {
        showMessage(message);
    }
}
