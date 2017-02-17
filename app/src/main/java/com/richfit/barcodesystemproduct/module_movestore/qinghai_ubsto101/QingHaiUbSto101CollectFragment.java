package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101.imp.QingHaiUBSto101CollectPresenterImp;
import com.richfit.domain.bean.RefDetailEntity;

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

    /**
     * 重写获取单条缓存，增加仓位的检查
     * @param batchFlag
     * @param location
     */
    @Override
    protected void getTransferSingle(String batchFlag, String location) {

        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请先选择单据行");
            return;
        }
        //检验是否选择了库存地点
        if (spInv.getSelectedItemPosition() == 0) {
            showMessage("请先选择库存地点");
            return;
        }

        //批次处理
        if (mIsOpenBatchManager)
            if (TextUtils.isEmpty(batchFlag)) {
                showMessage("请先输入批次");
                return;
            }
        if (TextUtils.isEmpty(location)) {
            showMessage("请先输入上架仓位");
            return;
        }

        if (location.length() > 10) {
            showMessage("您输入的仓位长度大于10位,请重新输入");
            return;
        }

        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final String invId = mInvDatas.get(spInv.getSelectedItemPosition()).invId;
        mPresenter.checkLocation("04", lineData.workId, invId, batchFlag, location);
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
