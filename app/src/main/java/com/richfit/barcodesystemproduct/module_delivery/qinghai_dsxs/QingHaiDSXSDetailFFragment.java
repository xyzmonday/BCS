package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs;

import com.richfit.barcodesystemproduct.module_delivery.basedetail.BaseDSDetailFragment;

/**
 * Created by monday on 2017/1/20.
 */

public class QingHaiDSXSDetailFFragment extends BaseDSDetailFragment{


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getSubFunName() {
        return "销售出库";
    }
}
