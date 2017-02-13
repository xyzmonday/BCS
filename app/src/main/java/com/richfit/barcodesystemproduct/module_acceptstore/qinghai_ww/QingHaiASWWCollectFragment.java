package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;

/**
 * Created by monday on 2017/1/19.
 */

public class QingHaiASWWCollectFragment extends BaseASCollectFragment{

    @Override
    public void initInjector() {

    }

    @Override
    protected int getOrgFlag() {
        return getInteger(R.integer.orgNorm);
    }
}
