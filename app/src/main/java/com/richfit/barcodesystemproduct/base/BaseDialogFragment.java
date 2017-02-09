package com.richfit.barcodesystemproduct.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.SampleApplicationLike;
import com.richfit.barcodesystemproduct.di.component.DaggerFragmentComponent;
import com.richfit.barcodesystemproduct.di.component.FragmentComponent;
import com.richfit.barcodesystemproduct.di.module.FragmentModule;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by monday on 2016/11/21.
 */

public abstract class BaseDialogFragment<T extends IPresenter> extends DialogFragment implements BaseView {

    @Inject
    protected T mPresenter;

    protected FragmentComponent mFragmentComponent;

    protected Activity mActivity;

    protected View mView;

    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //启用窗体的扩展特性。
        getDialog().requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        mView = inflater.inflate(getContentId(), container, false);
        mUnbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .appComponent(SampleApplicationLike.getAppComponent())
                .build();
        initInjector();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView();
        initEvent();
        initData();

        //设置自定义的title  layout
        getDialog().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
               R.layout.item_show_input_title);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    protected abstract void initVariable(Bundle savedInstanceState);

    protected abstract void initInjector();

    protected abstract int getContentId();

    protected abstract void initView();

    protected abstract void initEvent();

    protected abstract void initData();

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
