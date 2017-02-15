package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingHaiUbSto351DetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_movestore.basedetail.BaseMSDetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351.imp.QingHaiUnSto351DetailPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

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
        QingHaiUbSto351DetailAdapter adapter = new QingHaiUbSto351DetailAdapter(mActivity, allNodes,
                mSubFunEntity.parentNodeConfigs, mSubFunEntity.childNodeConfigs,
                mCompanyCode);
        mRecycleView.setAdapter(adapter);
        adapter.setOnItemEditAndDeleteListener(this);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && QingHaiUbSto351DetailAdapter.class.isInstance(adapter)) {
            QingHaiUbSto351DetailAdapter detailAdapter = (QingHaiUbSto351DetailAdapter) adapter;
            detailAdapter.removeNodeByPosition(position);
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
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        menus.get(0).transToSapFlag = "01";
        menus.get(1).transToSapFlag = "05";
        return menus.subList(0,2);
    }

    @Override
    protected int getSubmitType() {
        return getInteger(R.integer.detailSubmitDataType0);
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
