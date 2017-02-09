package com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy;

import com.richfit.barcodesystemproduct.module_delivery.baseedit.BaseDSEditFragment;

/**
 * Created by monday on 2017/1/17.
 */

public class QingYangDSYEditFragment extends BaseDSEditFragment {
    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getInvType() {
        return "01";
    }
}
