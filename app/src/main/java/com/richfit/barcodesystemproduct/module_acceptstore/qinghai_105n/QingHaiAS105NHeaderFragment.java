package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * 青海105入库非必检抬头界面
 * Created by monday on 2017/2/20.
 */

public class QingHaiAS105NHeaderFragment extends BaseASHeaderFragment{

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected void initView() {
        llSupplier.setVisibility(View.VISIBLE);
        super.initView();
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "1";
    }

}
