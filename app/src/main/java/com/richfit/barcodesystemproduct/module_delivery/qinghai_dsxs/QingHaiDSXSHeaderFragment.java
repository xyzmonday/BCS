package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_delivery.baseheader.BaseDSHeaderFragment;
import com.richfit.domain.bean.RefNumEntity;

import java.util.List;

/**
 * Created by monday on 2017/1/20.
 */

public class QingHaiDSXSHeaderFragment extends BaseDSHeaderFragment{

    @NonNull
    @Override
    protected String getBizType() {
        return mBizType;
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "1";
    }

    @Override
    public void initView() {
        llCustomer.setVisibility(View.VISIBLE);
        super.initView();
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void loadRefNumListSuccess(List<RefNumEntity> list) {

    }

    @Override
    public void loadRefNumListFail(String message) {

    }
}
