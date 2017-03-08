package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.basedetail.BaseASDetailFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n.imp.QingHaiAS105NDetailPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BottomMenuEntity;

import java.util.List;

/**
 * 青海物资入库105数据明细界面
 * Created by monday on 2017/2/20.
 */

public class QingHaiAS105NDetailFragment extends BaseASDetailFragment<QingHaiAS105NDetailPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        if (mAdapter != null) {
            mAdapter.removeNodeByPosition(position);
        }
    }

    @Override
    public void submitBarcodeSystemSuccess() {
        showSuccessDialog(mTransNum);
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        if (TextUtils.isEmpty(message)) {
            message += "过账失败";
        }
        showErrorDialog(message);
        mTransNum = "";
    }

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
        if (mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void submitSAPFail(String[] messages) {
        showErrorDialog(messages);
        mInspectionNum = "";
    }

    @Override
    public void upAndDownLocationFail(String[] messages) {

    }

    @Override
    public void upAndDownLocationSuccess() {

    }

    @Override
    protected String getSubFunName() {
        return "物资入库105非必检";
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
