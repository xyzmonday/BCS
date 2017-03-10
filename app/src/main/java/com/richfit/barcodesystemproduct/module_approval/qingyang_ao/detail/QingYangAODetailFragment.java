package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingYangAOAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail.imp.ApprovalOtherDetailPresenterImp;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by monday on 2016/11/24.
 */

public class QingYangAODetailFragment extends BaseFragment<ApprovalOtherDetailPresenterImp, RefDetailEntity>
        implements IApprovalOtherDetailView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.data_details_recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;
    @BindView(R.id.root_id)
    LinearLayout mExtraContainer;

    QingYangAOAdapter mAdapter;

    @Override
    protected int getContentId() {
        return R.layout.fragment_qingyang_ao_detail;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager lm = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(lm);
        mRecycleView.setItemAnimator(new FadeInDownAnimator());
        //获取配置信息(注意明细界面的额外字段不能每一次页面可见去获取)
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.DETAIL_PARENT_NODE_CONFIG_TYPE,
                Global.DETAIL_CHILD_NODE_CONFIG_TYPE);
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        //注意没有仓位
        mSubFunEntity.parentNodeConfigs = configs.get(0);
        createExtraUI(mSubFunEntity.parentNodeConfigs, EXTRA_HORIZONTAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.parentNodeConfigs = null;
    }

    @Override
    public void initDataLazily() {
        if (mRefData == null) {
            showMessage("请现在抬头界面获取单据数据");
            return;
        }

        if (TextUtils.isEmpty(mRefData.recordNum)) {
            showMessage("请现在抬头界面输入验收单号");
            return;
        }

        if (TextUtils.isEmpty(mRefData.bizType)) {
            showMessage("未获取到业务类型");
            return;
        }

        if (!checkExtraData(mSubFunEntity.headerConfigs)) {
            showMessage("请先在抬头界面输入必要的字段信息");
            return;
        }
        startAutoRefresh();
    }

    @Override
    public void startAutoRefresh() {
        mSwipeRefreshLayout.postDelayed((() -> {
            mHorizontalScroll.scrollTo((int) (mExtraContainer.getWidth() / 2.0f - UiUtil.getScreenWidth(mActivity) / 2.0f), 0);
            mSwipeRefreshLayout.autoRefresh();
        }), 50);
    }

    @Override
    public void showNodes(List<RefDetailEntity> nodes) {
        if (mAdapter == null) {
            mAdapter = new QingYangAOAdapter(mActivity,
                    R.layout.item_qingyang_ao_detail, nodes,
                    mSubFunEntity.parentNodeConfigs, null, mCompanyCode);
            mAdapter.setOnItemEditAndDeleteListener(this);
            mRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.addAll(nodes);
        }
    }

    /**
     * 自动下拉刷新
     */
    @Override
    public void onRefresh() {
        //单号
        final String recordNum = mRefData.recordNum;
        //业务类型
        final String bizType = mRefData.bizType;
        //单据类型
        final String refType = mRefData.refType;
        //移动类型
        final String moveType = mRefData.moveType;
        //获取缓存累计数量不对
        mPresenter.getReference(mRefData, recordNum, refType, bizType, moveType,"", Global.USER_ID);
    }

    @Override
    public void setRefreshing(boolean isSuccess, String message) {
        //不论成功或者失败都应该关闭下拉加载动画
        mSwipeRefreshLayout.setRefreshing(false);
        showMessage(message);
    }

    /**
     * 调用接口删除单条数据，删除成功后修改本地内存数据，并刷新
     *
     * @param node
     * @param position
     */
    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        if ("Y".equals(node.lineInspectFlag)) {
            showMessage("该行已经过账,不允许删除");
            return;
        }
        if (TextUtils.isEmpty(node.totalQuantity) || "0".equals(node.totalQuantity)) {
            showMessage("该行还未进行数据采集!");
            return;
        }
        mPresenter.deleteNode("N", mRefData.recordNum, node.lineNum, node.refLineId,
                mRefData.refType, mRefData.bizType, Global.USER_ID, position, mCompanyCode);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        if (mAdapter != null) {
            mAdapter.removeNodeByPosition(position);
        }
    }

    @Override
    public void deleteNodeFail(String message) {
        showMessage(message);
    }


    @Override
    public void editNode(final RefDetailEntity node, int position) {
        if ("Y".equals(node.lineInspectFlag)) {
            showMessage("该行已经过账,不允许编辑");
            return;
        }

        if (TextUtils.isEmpty(node.totalQuantity) || "0".equals(node.totalQuantity)) {
            showMessage("该行还未进行数据采集!");
            return;
        }

        mPresenter.editNode(node, mCompanyCode, mBizType, mRefType, "验收结果修改", position);
    }

    @Override
    public boolean checkDataBeforeOperationOnDetail() {
        if (mRefData == null) {
            showMessage("请先获取验收清单");
            return false;
        }
        if (TextUtils.isEmpty(mRefData.refCodeId)) {
            showMessage("请先在抬头界面获取单据数据");
            return false;
        }

        if (TextUtils.isEmpty(mBizType)) {
            showMessage("未获取到业务类型");
            return false;
        }

        if (TextUtils.isEmpty(mRefType)) {
            showMessage("未获取到单据类型");
            return false;
        }

        if (mRefData.billDetailList == null || mRefData.billDetailList.size() == 0) {
            showMessage("该验收清单没有明细数据,不需要过账");
            return false;
        }
        return true;
    }

    /**
     * 显示过账，数据上传等菜单对话框
     *
     * @param companyCode
     */
    @Override
    public void showOperationMenuOnDetail(final String companyCode) {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mActivity);
        dialog.setTitle("提示");
        dialog.setMessage("您真的需要过账该张验收单据吗?");
        dialog.setPositiveButton("确定", (dialogInterface, i) -> {
            uploadCollectedData();
            dialogInterface.dismiss();
        });
        dialog.setNegativeButton("取消", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        dialog.show();
    }

    public void uploadCollectedData() {
        mPresenter.uploadCollectionData(mRefData.recordNum, mRefData.refCodeId,
                mBizType, mRefType, mRefData.inspectionType, mRefData.voucherDate, Global.USER_ID, false);
    }

    @Override
    public void showSubmitSuccessMessage(String message) {
        //清除界面显示的历史明细
        if (mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
        new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("温馨提示")
                .setContentText(message)
                .show();
        //跳转到抬头界面,并且清除抬头界面的字段信息。
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void submitDataFail(String message) {
        showMessage(message);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_SAVE_COLLECTION_DATA_ACTION:
                uploadCollectedData();
                break;
        }
        super.retry(retryAction);
    }

}
