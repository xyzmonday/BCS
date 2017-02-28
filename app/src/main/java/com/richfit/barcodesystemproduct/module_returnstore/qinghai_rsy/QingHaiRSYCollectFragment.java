package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsy;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsy.imp.QingHaiRSYCollectPresenterImp;

/**
 * Created by monday on 2017/2/27.
 */

public class QingHaiRSYCollectFragment extends BaseASCollectFragment<QingHaiRSYCollectPresenterImp> {


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        tvActQuantityName.setText("应退数量");
        tvQuantityName.setText("实退数量");
        super.initView();
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> getTransferSingle(getString(etBatchFlag), location));
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
