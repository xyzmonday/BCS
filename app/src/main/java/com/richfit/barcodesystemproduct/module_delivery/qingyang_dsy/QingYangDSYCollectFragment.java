package com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy;

import com.richfit.barcodesystemproduct.module_delivery.basecollect.BaseDSCollectFragment;

/**
 * Created by monday on 2017/1/17.
 */

public class QingYangDSYCollectFragment extends BaseDSCollectFragment {

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
