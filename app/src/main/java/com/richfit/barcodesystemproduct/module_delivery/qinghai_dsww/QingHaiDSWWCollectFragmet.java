package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.BaseDSCollectFragment;

/**
 * Created by monday on 2017/3/5.
 */

public class QingHaiDSWWCollectFragmet extends BaseDSCollectFragment {

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
        return getString(R.string.inventoryQueryTypeSAPLocation);
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
