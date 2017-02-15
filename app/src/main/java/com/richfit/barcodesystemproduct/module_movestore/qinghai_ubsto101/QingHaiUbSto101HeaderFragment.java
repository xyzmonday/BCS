package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * 青海移库转储101抬头界面(采购订单)
 * Created by monday on 2017/2/15.
 */

public class QingHaiUbSto101HeaderFragment extends BaseASHeaderFragment{

    @Override
    protected void initView() {
        super.initView();
        llSendWork.setVisibility(View.VISIBLE);
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "3";
    }

}
