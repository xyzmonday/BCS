package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiAS103HeaderFragment extends BaseASHeaderFragment{

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected void initView() {
        super.initView();
        llSupplier.setVisibility(View.VISIBLE);
        llSendWork.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "1";
    }


}
