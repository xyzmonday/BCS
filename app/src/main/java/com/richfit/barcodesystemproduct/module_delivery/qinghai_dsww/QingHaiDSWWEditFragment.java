package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_delivery.baseedit.BaseDSEditFragment;

/**
 * Created by monday on 2017/1/20.
 */

public class QingHaiDSWWEditFragment extends BaseDSEditFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getInvType() {
        return "01";
    }

    @Override
    protected String getInventoryQueryType() {
        return getString(R.string.inventoryQueryTypePrecise);
    }
}
