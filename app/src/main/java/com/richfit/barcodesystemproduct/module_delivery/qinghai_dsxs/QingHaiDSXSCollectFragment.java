package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs;

import com.richfit.barcodesystemproduct.module_delivery.basecollect.BaseDSCollectFragment;

/**
 * Created by monday on 2017/1/20.
 */

public class QingHaiDSXSCollectFragment extends BaseDSCollectFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getInvType() {
        return "01";
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}