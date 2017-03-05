package com.richfit.barcodesystemproduct.module_movestore.qinghai_311n;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingHaiNMS311DetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module_movestore.basedetail_n.BaseNMSDetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_311n.imp.QingHaiNMS311DetailPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monday on 2017/2/16.
 */

public class QingHaiNMS311DetailFragment extends BaseNMSDetailFragment<QingHaiNMS311DetailPresenterImp>{

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        sendBatchFlag.setText("批次");
        recBatchFlag.setVisibility(View.GONE);
        super.initView();
    }

    @Override
    public void showNodes(List<RefDetailEntity> allNodes) {
        for (RefDetailEntity node : allNodes) {
            if (!TextUtils.isEmpty(node.transId)) {
                mTransId = node.transId;
                break;
            }
        }
        QingHaiNMS311DetailAdapter adapter = new QingHaiNMS311DetailAdapter(mActivity,
                R.layout.base_nms_detail_item, allNodes, mConfigs, null, mCompanyCode);
        mRecycleView.setAdapter(adapter);
        adapter.setOnItemEditAndDeleteListener(this);
    }

    @Override
    public void editNode(final RefDetailEntity node, int position) {
        String state = (String) SPrefUtil.getData(mBizType,"0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许删除");
            return;
        }
        //获取与该子节点的物料编码和发出库位一致的发出仓位和接收仓位列表
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && QingHaiNMS311DetailAdapter.class.isInstance(adapter)) {
            QingHaiNMS311DetailAdapter detailAdapter = (QingHaiNMS311DetailAdapter) adapter;
            ArrayList<String> sendLocations = detailAdapter.getLocations(node.materialNum, node.invId, position, 0);
            ArrayList<String> recLocations = detailAdapter.getLocations(node.materialNum, node.invId, position, 1);
            mPresenter.editNode(sendLocations, recLocations, node, EditActivity.class, mCompanyCode,
                    mBizType, mRefType, getSubFunName());
        }
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && QingHaiNMS311DetailAdapter.class.isInstance(adapter)) {
            QingHaiNMS311DetailAdapter detailAdapter = (QingHaiNMS311DetailAdapter) adapter;
            detailAdapter.removeItemByPosition(position);
            int itemCount = detailAdapter.getItemCount();
            if (itemCount == 0) {
                mExtraContainer.setVisibility(View.INVISIBLE);
            }
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
        return "311无参考";
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
