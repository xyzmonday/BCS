package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_movestore.baseedit_n.BaseNMSEditFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.imp.QingHaiNMS301EditPresenterImp;

/**
 * Created by monday on 2017/2/8.
 */

public class QingYangNMS301EditFragment extends BaseNMSEditFragment<QingHaiNMS301EditPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getInvType() {
        return getString(R.string.invTypeDaiGuan);
    }

    @Override
    protected String getInventoryQueryType() {
        return getString(R.string.inventoryQueryTypePrecise);
    }
}
