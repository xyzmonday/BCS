package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.adapter.QingHaiUbSto351DetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_movestore.basedetail.BaseMSDetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351.imp.QingHaiUnSto351DetailPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monday on 2017/2/10.
 */

public class QingHaiUbSto351DetailFragment extends BaseMSDetailFragment<QingHaiUnSto351DetailPresenterImp> {


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    public void showNodes(List<RefDetailEntity> allNodes) {
        for (RefDetailEntity node : allNodes) {
            if (!TextUtils.isEmpty(node.transId)) {
                mTransId = node.transId;
                break;
            }
        }
        setRefreshing(true, "加载明细成功");
        if(mAdapter == null) {
            mAdapter = new QingHaiUbSto351DetailAdapter(mActivity, allNodes,
                    mSubFunEntity.parentNodeConfigs, mSubFunEntity.childNodeConfigs,
                    mCompanyCode);
            mRecycleView.setAdapter(mAdapter);
            mAdapter.setOnItemEditAndDeleteListener(this);
        }else {
            mAdapter.addAll(allNodes);
        }
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        if (mAdapter != null) {
            mAdapter.removeNodeByPosition(position);
        }
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
        mRefData = null;
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    /**
     * 第二步(Transfer 05)失败后显示错误列表
     * @param messages
     */
    @Override
    public void submitSAPFail(String[] messages) {
        showErrorDialog(messages);
        mInspectionNum = "";
    }

    @Override
    protected String getSubFunName() {
        return "351移库";
    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> tmp = super.provideDefaultBottomMenu();
        tmp.get(0).transToSapFlag = "01";
        tmp.get(2).transToSapFlag = "05";
        //注意351是发出
        ArrayList menus = new ArrayList();
        menus.add(tmp.get(0));
        menus.add(tmp.get(2));
        return menus;
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
