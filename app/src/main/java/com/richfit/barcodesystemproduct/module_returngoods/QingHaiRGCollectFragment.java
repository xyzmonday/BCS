package com.richfit.barcodesystemproduct.module_returngoods;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.BaseDSCollectFragment;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.imp.DSCollectPresenterImp;

/**
 * Created by monday on 2017/2/23.
 */

public class QingHaiRGCollectFragment extends BaseDSCollectFragment<DSCollectPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        quantityName.setText("应退数量");
        actQuantityName.setText("实退数量");
        super.initView();
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
        return 0;
    }
}
