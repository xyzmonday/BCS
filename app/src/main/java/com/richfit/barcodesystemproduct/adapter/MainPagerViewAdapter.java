package com.richfit.barcodesystemproduct.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.richfit.barcodesystemproduct.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * http://blog.csdn.net/z13759561330/article/details/40737381
 * http://blog.sina.com.cn/s/blog_783ede03010173b4.html
 * Created by monday on 2016/8/2.
 * 这里需要注意的是：当点击侧边栏切换子菜单的时候，mFragments的页面（mFragment）或者
 * 说adapter的数据源发生了变化。此时，我们调用notifyDataSetChanged方法，发现
 * 页面并没有刷新。查看源码发现notifyDataSetChanged最终回调了viewpager的onChange
 * 方法，而该方法会根据getItemPosition方法的返回值判断是否需要刷新。另外，就是FragmentManager
 * 会缓存一个数据源，所以在刷新之前需要清空缓存。
 */

public class MainPagerViewAdapter<T extends BaseFragment> extends FragmentPagerAdapter {

    private FragmentManager fm;
    private List<T> mFragments;

    public MainPagerViewAdapter(FragmentManager fm, List<T> fragments) {
        super(fm);
        this.fm = fm;
        this.mFragments = fragments;
    }

    public MainPagerViewAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.mFragments = new ArrayList<>();
    }

    public void addFragmentAndTitle(T fragment) {
        mFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position % mFragments.size());
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position % mFragments.size()).getTabTitle();
    }

    public void setFragments(List<T> fragments) {
        if (this.mFragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.mFragments) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
