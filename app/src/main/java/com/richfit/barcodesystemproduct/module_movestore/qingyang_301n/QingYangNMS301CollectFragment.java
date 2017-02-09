package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.module_movestore.basecollect_n.BaseNMSCollectFragment;

/**
 * Created by monday on 2017/2/8.
 */

public class QingYangNMS301CollectFragment extends BaseNMSCollectFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected boolean checkHeaderData() {
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先选择发出工厂");
            return false;
        }

        if (TextUtils.isEmpty(mRefData.invId)) {
            showMessage("请先选择发出库位");
            return false;
        }

        if (TextUtils.isEmpty(mRefData.recWorkId)) {
            showMessage("请先选择接收工厂");
            return false;
        }

        if (TextUtils.isEmpty(mRefData.recInvId)) {
            showMessage("请先选择接收库位");
            return false;
        }
        return true;
    }

    @Override
    protected String getInvType() {
        //代管库存
        return "0";
    }

    @Override
    protected boolean getWMOpenFlag() {
        return false;
    }

    @Override
    protected int getOrgFlag() {
        return 1;
    }

    @Override
    protected void checkLocation() {
        checkLocationSuccess();
    }
}
