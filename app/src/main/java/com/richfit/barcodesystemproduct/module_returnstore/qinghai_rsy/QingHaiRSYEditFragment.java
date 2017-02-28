package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsy;

import com.richfit.barcodesystemproduct.module_acceptstore.baseedit.BaseASEditFragment;
import com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsy.imp.QingHaiRSYEditPresenterImp;

/**
 * Created by monday on 2017/2/27.
 */

public class QingHaiRSYEditFragment extends BaseASEditFragment<QingHaiRSYEditPresenterImp> {


    @Override
    protected void initView() {
        actQuantityName.setText("应退数量");
        quantityName.setText("实退数量");
        super.initView();
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }
}
