package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_movestore.basecollect.BaseMSCollectFragment;

/**
 * Created by monday on 2017/2/10.
 */

public class QingHaiUbSto351CollectFragment extends BaseMSCollectFragment{

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getInvType() {
        return getString(R.string.invTypeNorm);
    }

    @Override
    protected String getInventoryQueryType() {
        return getString(R.string.inventoryQueryTypeSAPLocation);
    }

    @Override
    protected int getOrgFlag() {
        return getInteger(R.integer.orgNorm);
    }

}
