package com.richfit.barcodesystemproduct.module_movestore.qinghai_311n;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_movestore.baseedit_n.BaseNMSEditFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_311n.imp.QingHaiNMS311EditPresenterImp;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiNMS311EditFragment extends BaseNMSEditFragment<QingHaiNMS311EditPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    public boolean checkCollectedDataBeforeSave() {
        final String recLocation = getString(etRecLoc);
        if(TextUtils.isEmpty(recLocation) || recLocation.length() > 10) {
            showMessage("您输入的接收仓位不合理");
            return false;
        }
        return super.checkCollectedDataBeforeSave();
    }

    @Override
    protected String getInvType() {
        return getString(R.string.invTypeNorm);
    }

    @Override
    protected String getInventoryQueryType() {
        return getString(R.string.inventoryQueryTypeSAPLocation);
    }

}
