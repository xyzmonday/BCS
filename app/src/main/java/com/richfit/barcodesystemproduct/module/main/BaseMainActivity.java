package com.richfit.barcodesystemproduct.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.MainPagerViewAdapter;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.rxutils.RxCilck;
import com.richfit.common_lib.transformer.CubeTransformer;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.widget.NoScrollViewPager;

import butterknife.BindView;

/**
 * Created by monday on 2017/3/10.
 */

public abstract class BaseMainActivity<P extends MainContract.Presenter> extends BaseActivity<P> implements MainContract.View,
        ViewPager.OnPageChangeListener {

    /*当前选中的页签下表，用于恢复*/
    public static final String CURRENT_PAGE_INDEX_KEY = "current_page_index";


    @BindView(R.id.tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    NoScrollViewPager mViewPager;
    @BindView(R.id.floating_button)
    FloatingActionButton mFloatingButton;

    String mBizType;
    String mRefType;
    String mCompanyCode;
    String mModuleCode;
    String mCaption;
    String mSelectedRefLineNum;

    /*当前的显示页面*/
    int mCurrentPage = 0;

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }


    @Override
    public void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCompanyCode = bundle.getString(Global.EXTRA_COMPANY_CODE_KEY);
                mModuleCode = bundle.getString(Global.EXTRA_MODULE_CODE_KEY);
                mBizType = bundle.getString(Global.EXTRA_BIZ_TYPE_KEY);
                mRefType = bundle.getString(Global.EXTRA_REF_TYPE_KEY);
                mCaption = bundle.getString(Global.EXTRA_CAPTION_KEY);
                mSelectedRefLineNum = bundle.getString(Global.EXTRA_REF_LINE_NUM_KEY);
            }
        }
    }

    @Override
    protected void initViews() {
        setupToolBar(R.id.toolbar, R.id.toolbar_title, mCaption);
        /*设置viewPager*/
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(true, new CubeTransformer());
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void initEvent() {
        RxCilck.clicks(mFloatingButton).subscribe(a -> responseOnClick());
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setupMainContent(savedInstanceState);
    }


    /**
     * 设置主页面的内容
     */
    private void setupMainContent(Bundle savedInstanceState) {
        int currentPageIndex = savedInstanceState == null ? -1 :
                savedInstanceState.getInt(CURRENT_PAGE_INDEX_KEY, -1);
        mPresenter.setupMainContent(getSupportFragmentManager(), mCompanyCode,
                mModuleCode, mBizType, mRefType, mSelectedRefLineNum, currentPageIndex);
    }

    @Override
    public void showMainContent(MainPagerViewAdapter adapter, int currentPageIndex) {
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //默认显示第一个页签
        mCurrentPage = currentPageIndex == -1 ? 0 : currentPageIndex;
        mViewPager.setCurrentItem(mCurrentPage);
        //如果默认显示的是抬头页面，那么根据isNeedShowFloatingButton决定是否显示按钮
        isShowFloatingButton();
    }

    private void isShowFloatingButton() {
        final BaseFragment fragment = getFragmentByPosition(mCurrentPage);
        if (fragment == null)
            return;
        mFloatingButton.setVisibility(fragment.isNeedShowFloatingButton() ?
                View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setupMainContentFail(String message) {
        showMessage(message);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //当该Activity从后台再次进入前台的时候(也就是后台的Activity已经onPause了)，那么根据需求需要
        //Fragment再次执行懒加载
        BaseFragment fragment = getFragmentByPosition(mCurrentPage);
        if (fragment != null && fragment.getFragmentType() == BaseFragment.DETAIL_FRAGMENT_INDEX) {
            getFragmentByPosition(mCurrentPage).setUserVisibleHint(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            exitBy2Click();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取一个Fragment实例对象
     */
    public BaseFragment getFragmentByPosition(final int position) {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null && MainPagerViewAdapter.class.isInstance(adapter)) {
            MainPagerViewAdapter mainPagerViewAdapter = (MainPagerViewAdapter) adapter;
            if (position < 0 || position > mainPagerViewAdapter.getCount() - 1)
                return null;
            return (BaseFragment) mainPagerViewAdapter.getItem(position);
        }
        return null;
    }

    /**
     * 设置viewpager的当前显示页
     */
    public void showFragmentByPosition(final int position) {
        if (position < 0 || position >= mViewPager.getAdapter().getCount())
            return;
        mViewPager.setCurrentItem(position);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        isShowFloatingButton();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 响应FloatingButton的点击事件
     */
    private void responseOnClick() {
        final BaseFragment fragment = getFragmentByPosition(mCurrentPage);
        if (fragment == null)
            return;
        switch (fragment.getFragmentType()) {
            //抬头界面
            case BaseFragment.HEADER_FRAGMENT_INDEX:
                if (fragment.checkDataBeforeOperationOnHeader()) {
                    fragment.operationOnHeader(mCompanyCode);
                }

                break;
            //数据明细界面
            case BaseFragment.DETAIL_FRAGMENT_INDEX:
                if (fragment.checkDataBeforeOperationOnDetail()) {
                    fragment.showOperationMenuOnDetail(mCompanyCode);
                }
                break;
            //数据采集界面
            case BaseFragment.COLLECT_FRAGMENT_INDEX:
                //这里要兼容验收模块拍照，情景是如果该行采集网拍照，然后回退之后，在接着拍照
                fragment.showOperationMenuOnCollection(mCompanyCode);
                break;
        }
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
    public void handleBarCodeScanResult(String type, String[] list) {
        final BaseFragment fragment = getFragmentByPosition(mCurrentPage);
        if (fragment == null) {
            return;
        }
        final int fragmentType = fragment.getFragmentType();
        //物料
        if (list.length > 2 && fragmentType == BaseFragment.COLLECT_FRAGMENT_INDEX) {
            fragment.handleBarCodeScanResult(type, list);
            //单据
        } else if (list.length == 1 && fragmentType == BaseFragment.HEADER_FRAGMENT_INDEX) {
            fragment.handleBarCodeScanResult(type, list);
            //仓位
        } else if (list.length == 2 && fragmentType == BaseFragment.COLLECT_FRAGMENT_INDEX) {
            fragment.handleBarCodeScanResult(type, list);
        } else {
            fragment.handleBarCodeScanResult(type, list);
        }
    }


}
