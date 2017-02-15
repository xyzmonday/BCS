package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101.imp.QingHaiUBSto101CollectPresenterImp;

/**
 * Created by monday on 2017/2/15.
 */

public class QingHaiUbSto101CollectFragment extends BaseASCollectFragment<QingHaiUBSto101CollectPresenterImp> {

    @Override
    protected void initView() {
        super.initView();
        tvWorkName.setText("接收工厂");
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> {
            hideKeyboard(view);
            getTransferSingle(getString(etBatchFlag),location);
        });
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
