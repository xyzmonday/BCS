package com.richfit.barcodesystemproduct.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.MainPagerViewAdapter;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.rxutils.RxCilck;
import com.richfit.common_lib.transformer.CubeTransformer;

import butterknife.BindView;

/**
 * Created by monday on 2016/11/10.
 */

public class MainActivity extends BaseActivity<MainPresenterImp> implements MainContract.View,
        ViewPager.OnPageChangeListener {

    public static final String EXTRA_COMPANY_CODE_KEY = "extra_company_code_key";
    public static final String EXTRA_MODULE_CODE_KEY = "extra_module_code_key";
    public static final String EXTRA_BIZ_TYPE_KEY = "extra_biz_type_key";
    public static final String EXTRA_REF_TYPE_KEY = "extra_ref_type_key";
    public static final String EXTRA_CAPTION_KEY = "extra_caption_key";
    /*当前选中的页签下表，用于恢复*/
    public static final String CURRENT_PAGE_INDEX_KEY = "current_page_index";


    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.floating_button)
    FloatingActionButton mFloatingButton;

    String mBizType;
    String mRefType;
    String mCompanyCode;
    String mModuleCode;
    String mCaption;

    /*当前的显示页面*/
    int mCurrentPage = 0;

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCompanyCode = bundle.getString(EXTRA_COMPANY_CODE_KEY);
                mModuleCode = bundle.getString(EXTRA_MODULE_CODE_KEY);
                mBizType = bundle.getString(EXTRA_BIZ_TYPE_KEY);
                mRefType = bundle.getString(EXTRA_REF_TYPE_KEY);
                mCaption = bundle.getString(EXTRA_CAPTION_KEY);
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
                mModuleCode, mBizType, mRefType, currentPageIndex);
    }

    @Override
    public void showMainContent(MainPagerViewAdapter adapter, int currentPageIndex) {
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //默认显示第一个页签
        mCurrentPage = currentPageIndex == -1 ? 0 : currentPageIndex;
        mViewPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void setupMainContentFail(String message) {
        showMessage(message);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mCurrentPage == BaseFragment.DETAIL_FRAGMENT_INDEX) {
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

}
