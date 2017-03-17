package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103;

import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseedit.BaseASEditFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103.imp.QingHaiAS103EditPresenterImp;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiAS103EditFragment extends BaseASEditFragment<QingHaiAS103EditPresenterImp> {


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        etLocation.setEnabled(false);
        llLocation.setVisibility(View.GONE);
        llLocationQuantity.setVisibility(View.GONE);
        super.initView();
    }

}
