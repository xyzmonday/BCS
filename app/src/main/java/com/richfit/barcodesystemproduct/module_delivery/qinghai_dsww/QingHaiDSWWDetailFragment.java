package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_delivery.basedetail.BaseDSDetailFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww.imp.QingHaiDSWWDetailPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BottomMenuEntity;

import java.util.List;

/**
 * Created by monday on 2017/3/5.
 */

public class QingHaiDSWWDetailFragment extends BaseDSDetailFragment<QingHaiDSWWDetailPresenterImp> {
    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected String getSubFunName() {
        return "委外出库";
    }

    /**
     * 1.过账。必须保证所有的明细行都完成了才能开始过账
     */
    protected void submit2BarcodeSystem(String tranToSapFlag) {
        if (mAdapter != null) {
            if (!mAdapter.isTransferValide()) {
                showMessage("您必须对所有的明细采集数据后，才能过账");
                return;
            }
        }
        super.submit2BarcodeSystem(tranToSapFlag);
    }

    /**
     * 第一步的过账(Transfer 01)成功后，将状态标识设置为1，
     * 本次出库不在允许对该张单据进行任何操作。
     */
    @Override
    public void submitBarcodeSystemSuccess() {
        showSuccessDialog(mTransNum);
    }

    /**
     * 第一步的过账(Transfer 01)失败后，必须清除状态标识。
     *
     * @param message
     */
    @Override
    public void submitBarcodeSystemFail(String message) {
        if (TextUtils.isEmpty(message)) {
            message += "过账失败";
        }
        showErrorDialog(message);
        mTransNum = "";
    }

    /**
     * 第二步(Transfer 05)成功后清除明细数据，跳转到抬头界面。
     */
    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
        if(mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    /**
     * 第二步(Transfer 05)失败后显示错误列表
     *
     * @param messages
     */
    @Override
    public void submitSAPFail(String[] messages) {
        showErrorDialog(messages);
        mInspectionNum = "";
    }


    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        menus.get(0).transToSapFlag = "01";
        menus.get(1).transToSapFlag = "05";
        return menus.subList(0, 2);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_TRANSFER_DATA_ACTION:
                submit2BarcodeSystem(mBottomMenus.get(0).transToSapFlag);
                break;
            case Global.RETRY_UPLOAD_DATA_ACTION:
                submit2SAP(mBottomMenus.get(1).transToSapFlag);
                break;
        }
        super.retry(retryAction);
    }

}
