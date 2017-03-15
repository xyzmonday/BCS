package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103;

import android.text.TextUtils;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingHaiAS103DetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.basedetail.BaseASDetailFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103.imp.QingHaiAS103DetailPresenterImp;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiAS103DetailFragment extends BaseASDetailFragment<QingHaiAS103DetailPresenterImp> {

    QingHaiAS103DetailAdapter mAdapter;

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        tvIsNLocation.setVisibility(View.GONE);
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
        if (mAdapter == null) {
            mAdapter = new QingHaiAS103DetailAdapter(mActivity, R.layout.base_as_detail_parent_item,
                    allNodes, mSubFunEntity.parentNodeConfigs, mSubFunEntity.childNodeConfigs, mCompanyCode);
            mRecycleView.setAdapter(mAdapter);
            mAdapter.setOnItemEditAndDeleteListener(this);
        } else {
            mAdapter.addAll(allNodes);
        }
    }

    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        if (!TextUtils.isEmpty(mTransNum)) {
            showMessage("已经过账,不允许修改");
            return;
        }
        if (TextUtils.isEmpty(node.transLineId)) {
            showMessage("该行还未进行数据采集");
            return;
        }
        mPresenter.deleteNode("N", node.transId, node.transLineId, node.locationId,
                mRefData.refType, mRefData.bizType, position, mCompanyCode);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        if (mAdapter != null) {
            mAdapter.removeNodeByPosition(position);
        }
    }

    @Override
    public void editNode(final RefDetailEntity node, int position) {
        if (!TextUtils.isEmpty(mTransNum)) {
            showMessage("本次入库已经过账,不允许在进行修改");
            return;
        }
        if (TextUtils.isEmpty(node.transLineId)) {
            showMessage("该行还未进行数据采集");
            return;
        }
        //获取与该子节点的物料编码和发出库位一致的发出仓位和接收仓位列表
        if (mAdapter != null) {
            ArrayList<String> locations = mAdapter.getLocations(position, 0);
            mPresenter.editNode(locations, null, node, mCompanyCode,
                    mBizType, mRefType, getSubFunName(), position);
        }
    }

    @Override
    public void submitBarcodeSystemSuccess() {
        showMessage("过账成功");
        showSuccessDialog(mTransNum);
        if (mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        showErrorDialog(message);
    }

    @Override
    public void submitSAPSuccess() {

    }

    @Override
    public void submitSAPFail(String[] messages) {

    }

    @Override
    public void upAndDownLocationFail(String[] messages) {

    }

    @Override
    public void upAndDownLocationSuccess() {

    }

    @Override
    protected String getSubFunName() {
        return "采购入库103";
    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        menus.get(0).transToSapFlag = "01";
        return menus.subList(0, 1);
    }
}


