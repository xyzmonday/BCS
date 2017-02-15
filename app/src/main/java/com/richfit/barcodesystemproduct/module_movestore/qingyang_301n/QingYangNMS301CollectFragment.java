package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_movestore.basecollect_n.BaseNMSCollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.imp.QingYangNMS301CollectPresenterImp;

/**
 * Created by monday on 2017/2/8.
 */

public class QingYangNMS301CollectFragment extends BaseNMSCollectFragment<QingYangNMS301CollectPresenterImp> {

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
    public boolean checkCollectedDataBeforeSave() {
        if(isWareHouseSame && TextUtils.isEmpty(getString(etRecLoc))) {
            showMessage("请输入接收仓位");
            return false;
        }

        if(mIsOpenBatchManager && TextUtils.isEmpty(getString(etRecBatchFlag))) {
            showMessage("请输入接收批次");
            return false;
        }
        return super.checkCollectedDataBeforeSave();
    }

    @Override
    protected String getInvType() {
        return getString(R.string.invTypeDaiGuan);
    }

    @Override
    protected String getInventoryType() {
        return getString(R.string.inventoryQueryTypePrecise);
    }

    @Override
    protected String getSpecialFlag() {
        return "N";
    }

    @Override
    protected boolean getWMOpenFlag() {
        return false;
    }

    @Override
    protected int getOrgFlag() {
        return getInteger(R.integer.orgSecond);
    }
}
