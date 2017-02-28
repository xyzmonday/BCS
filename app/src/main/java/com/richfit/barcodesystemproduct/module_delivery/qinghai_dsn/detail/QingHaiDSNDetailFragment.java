package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.detail;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.adapter.DSNDetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.detail.imp.QingHaiDSNDetailPresenterImp;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by monday on 2017/2/23.
 */

public class QingHaiDSNDetailFragment extends BaseFragment<QingHaiDSNDetailPresenterImp, RefDetailEntity>
        implements IQingHaiDSNDetailView, SwipeRefreshLayout.OnRefreshListener {

    private static final HashMap<String, Object> FLAGMAP = new HashMap<>();

    @BindView(R.id.data_details_recycle_view)
    protected RecyclerView mRecycleView;
    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;

    @BindView(R.id.root_id)
    protected LinearLayout mExtraContainer;

    protected String mTransId;
    protected String mTransNum;
    //父子节点的配置信息结合
    protected ArrayList<RowConfig> mConfigs;
    protected String mInspectionNum;
    protected List<BottomMenuEntity> mBottomMenus;

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected int getContentId() {
        return R.layout.fragment_base_dsn_detail;
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        mConfigs = new ArrayList<>();
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
                Global.DETAIL_PARENT_NODE_CONFIG_TYPE, Global.DETAIL_CHILD_NODE_CONFIG_TYPE);
    }

    @Override
    public void initDataLazily() {
        if (mRefData == null) {
            showMessage("请先在抬头界面选择工厂");
            return;
        }
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先在抬头界面选择工厂");
            return;
        }
        if (TextUtils.isEmpty(mRefData.costCenter)) {
            showMessage("请先在抬头界面输入成本中心");
            return;
        }
        startAutoRefresh();
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.parentNodeConfigs = configs.get(0);
        mSubFunEntity.childNodeConfigs = configs.get(1);
        mConfigs.clear();
        if (mSubFunEntity.parentNodeConfigs != null) {
            mConfigs.addAll(mSubFunEntity.parentNodeConfigs);
        }
        if (mSubFunEntity.childNodeConfigs != null) {
            mConfigs.addAll(mSubFunEntity.childNodeConfigs);
        }
        createExtraUI(mConfigs, EXTRA_HORIZONTAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.parentNodeConfigs = null;
        mSubFunEntity.childNodeConfigs = null;
    }

    @Override
    public void startAutoRefresh() {
        mSwipeRefreshLayout.postDelayed((() -> {
            mHorizontalScroll.scrollTo((int) (mExtraContainer.getWidth() / 2.0f - UiUtil.getScreenWidth(mActivity) / 2.0f), 0);
            mSwipeRefreshLayout.autoRefresh();
        }), 50);
    }


    @Override
    public void onRefresh() {
        String transferKey = (String) SPrefUtil.getData(mBizType, "0");
        if ("1".equals(transferKey)) {
            setRefreshing(false, "本次采集已经过账,请先进行数据上传操作");
            return;
        }
        //单据抬头id
        final String refCodeId = mRefData.refCodeId;
        //业务类型
        final String bizType = mRefData.bizType;
        //单据类型
        final String refType = mRefData.refType;
        //清除缓存id
        mTransId = "";
        //清除过账凭证
        mTransNum = "";
        //获取缓存
        mPresenter.getTransferInfo(refCodeId, bizType, refType, Global.USER_ID, mRefData.workId,
                mRefData.invId, mRefData.recWorkId, mRefData.recInvId);
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
        DSNDetailAdapter adapter = new DSNDetailAdapter(mActivity,
                R.layout.base_dsn_detail_parent_item, allNodes, mConfigs, null, mCompanyCode);
        mRecycleView.setAdapter(adapter);
        adapter.setOnItemEditAndDeleteListener(this);
    }

    @Override
    public void setRefreshing(boolean isSuccess, String message) {
        //不论成功或者失败都应该关闭下拉加载动画
        mSwipeRefreshLayout.setRefreshing(false);
        mExtraContainer.setVisibility(isSuccess ? View.VISIBLE : View.INVISIBLE);
        showMessage(message);
    }

    /**
     * 修改明细
     *
     * @param node
     * @param position
     */
    @Override
    public void editNode(final RefDetailEntity node, int position) {
        String state = (String) SPrefUtil.getData(mBizType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许删除");
            return;
        }
        //获取与该子节点的物料编码和发出库位一致的发出仓位和接收仓位列表
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && DSNDetailAdapter.class.isInstance(adapter)) {
            DSNDetailAdapter detailAdapter = (DSNDetailAdapter) adapter;
            ArrayList<String> sendLocations = detailAdapter.getLocations(position, 0);
            mPresenter.editNode(sendLocations, null, node, EditActivity.class, mCompanyCode,
                    mBizType, mRefType, "201无参考出库");
        }
    }


    /**
     * 删除明细数据
     *
     * @param node
     * @param position
     */
    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        String state = (String) SPrefUtil.getData(mBizType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许删除");
            return;
        }
        mPresenter.deleteNode("N", node.transId, node.transLineId, node.locationId,
                mRefData.refType, mRefData.bizType, position, mCompanyCode);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && DSNDetailAdapter.class.isInstance(adapter)) {
            DSNDetailAdapter detailAdapter = (DSNDetailAdapter) adapter;
            detailAdapter.removeItemByPosition(position);
            int itemCount = detailAdapter.getItemCount();
            if (itemCount == 0) {
                mExtraContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void deleteNodeFail(String message) {
        showMessage(message);
    }

    @Override
    public boolean checkDataBeforeOperationOnDetail() {
        if (mRefData == null) {
            showMessage("请先获取单据数据");
            return false;
        }
        if (TextUtils.isEmpty(mTransId)) {
            showMessage("未获取缓存标识");
            return false;
        }

        if (TextUtils.isEmpty(mRefData.voucherDate)) {
            showMessage("请先选择过账日期");
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
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
        GridView menu = (GridView) rootView.findViewById(R.id.gridview);
        mBottomMenus = provideDefaultBottomMenu();
        BottomMenuAdapter adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu, mBottomMenus);
        menu.setAdapter(adapter);

        final Dialog dialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
        dialog.setContentView(rootView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        menu.setOnItemClickListener((adapterView, view, position, id) -> {
            switch (position) {
                case 0:
                    //1.过账
                    submit2BarcodeSystem(mBottomMenus.get(position).transToSapFlag);
                    break;
                case 1:
                    submit2SAP(mBottomMenus.get(position).transToSapFlag);
                    break;
                case 3:
                    //转储
                    sapUpAndDownLocation(mBottomMenus.get(position).transToSapFlag);
                    break;
            }
            dialog.dismiss();
        });
    }

    /**
     * 1.过账
     */
    protected void submit2BarcodeSystem(String tranToSapFlag) {
        String transferFlag = (String) SPrefUtil.getData(mBizType, "0");
        if ("1".equals(transferFlag)) {
            showMessage("本次采集已经过账,请先进行数据上传操作");
            return;
        }
        FLAGMAP.clear();
        FLAGMAP.put("transToSapFlag", tranToSapFlag);
        mPresenter.submitData2BarcodeSystem(mTransId, mRefData.bizType, mRefType, Global.USER_ID,
                mRefData.voucherDate, FLAGMAP, createExtraHeaderMap());
    }

    @Override
    public void showTransferedVisa(String visa) {
        mTransNum = visa;
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


    /**
     * 2.数据上传
     */
    protected void submit2SAP(String tranToSapFlag) {
        if (TextUtils.isEmpty(mTransNum)) {
            showMessage("请先过账");
            return;
        }
        FLAGMAP.clear();
        FLAGMAP.put("transToSapFlag", tranToSapFlag);
        mPresenter.submitData2SAP(mTransId, mRefData.bizType, mRefType, Global.USER_ID,
                mRefData.voucherDate, FLAGMAP, createExtraHeaderMap());
    }

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void showInspectionNum(String inspectionNum) {
        mInspectionNum = inspectionNum;
    }

    @Override
    public void submitSAPFail(String[] messages) {
        showErrorDialog(messages);
        mInspectionNum = "";
    }


    private void sapUpAndDownLocation(String tranToSapFlag) {

    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        menus.get(0).transToSapFlag = "01";
        menus.get(1).transToSapFlag = "05";
        return menus.subList(0, 2);
    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(retryAction);
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
