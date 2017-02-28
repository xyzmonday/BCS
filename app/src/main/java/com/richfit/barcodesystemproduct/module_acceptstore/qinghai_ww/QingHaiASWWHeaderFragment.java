package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * Created by monday on 2017/2/28.
 */

public class QingHaiASWWHeaderFragment extends BaseASHeaderFragment {

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
