package com.richfit.barcodesystemproduct.module_movestore.qinghai_311n;

import android.text.TextUtils;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_movestore.basecollect_n.BaseNMSCollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_311n.imp.QingHaiNMS311CollectPresenterImp;

/**
 * Created by monday on 2017/2/16.
 */

public class QingHaiNMS311CollectFragment extends BaseNMSCollectFragment<QingHaiNMS311CollectPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        //工厂内移库不需要接收批次
        llRecBatch.setVisibility(View.GONE);
        tvSendBatchFlagName.setText("批次");
        super.initView();
    }

    /**
     * 如果打开了WM那么需要检查仓库号是否一致。
     * 对于工厂内的转储，没有接收工厂，那么接收工厂Id默认为发出工厂
     */
    @Override
    protected void checkWareHouseNum(int position) {
        if (position <= 0) {
            return;
        }
        final String workId = mRefData.workId;
        final String invCode = mSendInvs.get(position).invCode;
        final String recInvCode = mRefData.recInvCode;
        if (isOpenWM) {
            //没有打开WM，不需要检查ERP仓库号是否一致
            isWareHouseSame = true;
            return;
        }
        if (TextUtils.isEmpty(workId)) {
            showMessage("工厂为空");
            return;
        }
        if (TextUtils.isEmpty(invCode)) {
            showMessage("发出库位为空");
            return;
        }

        if (TextUtils.isEmpty(recInvCode)) {
            showMessage("接收库位为空");
            return;
        }
        mPresenter.checkWareHouseNum(isOpenWM, workId, invCode, workId, recInvCode, getOrgFlag());
    }

    @Override
    protected boolean checkHeaderData() {
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先选择工厂");
            return false;
        }

        if (TextUtils.isEmpty(mRefData.invId)) {
            showMessage("请先选择发出库位");
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
        //发出工厂
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先选择工厂");
            return false;
        }
        //检查批次
        if (mIsOpenBatchManager && TextUtils.isEmpty(getString(etSendBatchFlag))) {
            showMessage("批次为空");
            return false;
        }

        //检查接收
        final String recLocation = getString(etRecLoc);
        if (TextUtils.isEmpty(recLocation) || recLocation.length() > 10) {
            showMessage("接收仓位有误");
            return false;
        }

        if (isWareHouseSame && TextUtils.isEmpty(getString(etRecLoc))) {
            showMessage("请输入接收仓位");
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

    @Override
    protected boolean getWMOpenFlag() {
        return false;
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
