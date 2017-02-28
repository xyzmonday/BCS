package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs;

import com.richfit.barcodesystemproduct.module_delivery.basedetail.BaseDSDetailFragment;
import com.richfit.barcodesystemproduct.module_delivery.basedetail.imp.DSDetailPresenterImp;

/**
 * Created by monday on 2017/1/20.
 */

public class QingHaiDSXSDetailFFragment extends BaseDSDetailFragment<DSDetailPresenterImp>{


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getSubFunName() {
        return "销售出库";
    }

    @Override
    public void submitBarcodeSystemSuccess() {

    }

    @Override
    public void submitBarcodeSystemFail(String message) {

    }

    @Override
    public void submitSAPSuccess() {

    }

    @Override
    public void submitSAPFail(String[] messages) {

    }
}
