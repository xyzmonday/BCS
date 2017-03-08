package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n;

import android.text.TextUtils;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingYangNMS301DetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module_movestore.basedetail_n.BaseNMSDetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.imp.QingYangNMS301DetailPresenterImp;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 庆阳301数据过账只有一步
 * Created by monday on 2017/2/8.
 */

public class QingYangNMS301DetailFragment extends BaseNMSDetailFragment<QingYangNMS301DetailPresenterImp> {

    QingYangNMS301DetailAdapter mAdapter;

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
        if (mAdapter == null) {
            mAdapter = new QingYangNMS301DetailAdapter(mActivity,
                    R.layout.base_nms_detail_item, allNodes, mConfigs, null, mCompanyCode);
            mRecycleView.setAdapter(mAdapter);
            mAdapter.setOnItemEditAndDeleteListener(this);
        } else {
            mAdapter.addAll(allNodes);
        }
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        if (mAdapter != null) {
            mAdapter.removeItemByPosition(position);
            int itemCount = mAdapter.getItemCount();
            if (itemCount == 0) {
                mExtraContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void editNode(final RefDetailEntity node, int position) {
        if (!TextUtils.isEmpty(mTransNum)) {
            showMessage("本次入库已经过账,不允许在进行修改");
            return;
        }
        //获取与该子节点的物料编码和发出库位一致的发出仓位和接收仓位列表
        if (mAdapter != null) {
            ArrayList<String> sendLocations = mAdapter.getLocations(position, 0);
            ArrayList<String> recLocations = mAdapter.getLocations(position, 1);
            mPresenter.editNode(sendLocations, recLocations, node, EditActivity.class, mCompanyCode,
                    mBizType, mRefType, getSubFunName());
        }
    }

    @Override
    public void submitBarcodeSystemSuccess() {
        setRefreshing(false, "过账成功");
        showSuccessDialog(mTransNum);
        if (mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        showMessage(message);
    }

    @Override
    public void submitSAPSuccess() {

    }

    @Override
    public void showInspectionNum(String message) {
    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        return menus.subList(0, 1);
    }

    @Override
    protected String getSubFunName() {
        return "301无参考移库";
    }
}
