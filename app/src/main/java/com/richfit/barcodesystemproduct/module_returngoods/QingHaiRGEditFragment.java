package com.richfit.barcodesystemproduct.module_returngoods;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_delivery.baseedit.BaseDSEditFragment;

/**
 * Created by monday on 2017/2/23.
 */

public class QingHaiRGEditFragment extends BaseDSEditFragment {
    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getInvType() {
        return "采购退货";
    }

    @Override
    protected String getInventoryQueryType() {
        return getString(R.string.inventoryQueryTypeSAPLocation);
    }
}
