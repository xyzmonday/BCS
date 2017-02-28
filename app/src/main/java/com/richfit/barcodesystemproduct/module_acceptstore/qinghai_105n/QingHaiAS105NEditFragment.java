package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.module_acceptstore.baseedit.BaseASEditFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n.imp.QingHaiAS105NEditPresenterImp;

/**
 * Created by monday on 2017/2/20.
 */

public class QingHaiAS105NEditFragment extends BaseASEditFragment<QingHaiAS105NEditPresenterImp> {


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        final String location = getString(etLocation);
        if (TextUtils.isEmpty(location) || location.length() > 10) {
            showMessage("您输入的上架仓位有误");
            return false;
        }
        return super.checkCollectedDataBeforeSave();
    }
}
