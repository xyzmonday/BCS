package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.adapter.ASYDetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.basedetail.BaseASDetailFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww.imp.QingHaiASWWDetialPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * Created by monday on 2017/2/20.
 */

public class QingHaiASWWDetailFragment extends BaseASDetailFragment<QingHaiASWWDetialPresenterImp> {

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
        ASYDetailAdapter detailAdapter = new ASYDetailAdapter(mActivity,allNodes,mSubFunEntity.parentNodeConfigs,
                mSubFunEntity.childNodeConfigs,mCompanyCode);
        mRecycleView.setAdapter(detailAdapter);
        detailAdapter.setOnItemEditAndDeleteListener(this);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && ASYDetailAdapter.class.isInstance(adapter)) {
            ASYDetailAdapter detailAdapter = (ASYDetailAdapter) adapter;
            detailAdapter.removeNodeByPosition(position);
        }
    }

    @Override
    public void submitBarcodeSystemSuccess() {
        showSuccessDialog(mTransNum);
    }
    @Override
    public void submitBarcodeSystemFail(String message) {
        if (TextUtils.isEmpty(message)) {
            showMessage(message);
        } else {
            showErrorDialog(message);
        }
        mTransNum = "";
    }

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
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
        return "委外入库";
    }


    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        menus.get(0).transToSapFlag = "01";
        menus.get(1).transToSapFlag = "05";
        return menus.subList(0,2);
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