package com.richfit.barcodesystemproduct.module_returngoods;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_delivery.basedetail.BaseDSDetailFragment;
import com.richfit.barcodesystemproduct.module_returngoods.imp.QingHaiRGDetailPresenterImp;
import com.richfit.domain.bean.BottomMenuEntity;

import java.util.List;

/**
 * Created by monday on 2017/2/23.
 */

public class QingHaiRGDetailFragment extends BaseDSDetailFragment<QingHaiRGDetailPresenterImp> {

    @Override
    protected void initView() {
        actQuantityName.setText("实退数量");
        super.initView();
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
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
            showMessage(message);
        } else {
            showErrorDialog(message);
        }
        mTransNum = "";
    }

    /**
     * 第二步(Transfer 05)成功后清除明细数据，跳转到抬头界面。
     */
    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
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
    protected String getSubFunName() {
        return "采购退货";
    }
}
