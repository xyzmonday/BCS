package com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy;

import com.richfit.barcodesystemproduct.module_delivery.basedetail.BaseDSDetailFragment;

/**
 * Created by monday on 2017/1/17.
 */

public class QingYangDSYDetailFragment extends BaseDSDetailFragment{

    @Override
    protected String getSubFunName() {
        return "入库无参考";
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


}
