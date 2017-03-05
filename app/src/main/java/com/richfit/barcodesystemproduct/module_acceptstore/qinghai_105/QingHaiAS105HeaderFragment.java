package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * 青海物资入库105必检抬头界面(bizType为110)
 * Created by monday on 2017/3/1.
 */

public class QingHaiAS105HeaderFragment extends BaseASHeaderFragment{

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "1";
    }

    @Override
    protected void initView() {
        llSupplier.setVisibility(View.VISIBLE);
        super.initView();
    }
}
