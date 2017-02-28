package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww.imp.QingHaiASWWCollectPresenterImp;

/**
 * Created by monday on 2017/2/20.
 */

public class QingHaiASWWCollectFragment extends BaseASCollectFragment<QingHaiASWWCollectPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> {
            getTransferSingle(getString(etBatchFlag), location);
        });
    }


    @Override
    public boolean checkCollectedDataBeforeSave() {
        final String location = getString(etLocation);
        if (TextUtils.isEmpty(location)) {
            showMessage("请输入上架仓位");
            return false;
        }

        if(location.length() > 10) {
            showMessage("您输入的上架不合理");
            return false;
        }

        return super.checkCollectedDataBeforeSave();
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
