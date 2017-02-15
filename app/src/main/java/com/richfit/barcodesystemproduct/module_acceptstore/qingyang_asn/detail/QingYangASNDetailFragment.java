package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.detail;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.ASNDetailAdapter;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module.main.MainActivity;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.detail.imp.ASNDetailPresenterImp;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.dialog.ShowErrorMessageDialog;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.richfit.common_lib.utils.SPrefUtil.getData;

/**
 * Created by monday on 2016/11/27.
 */

public class QingYangASNDetailFragment extends BaseFragment<ASNDetailPresenterImp, RefDetailEntity>
        implements IASNDetailView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;
    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.data_details_recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.batchFlag)
    TextView tvBatchFlag;
    @BindView(R.id.location)
    TextView tvLocation;
    @BindView(R.id.root_id)
    LinearLayout mExtraContainer;

    String mTransId;
    String mTransNum;
    //父子节点的配置信息结合
    ArrayList<RowConfig> mConfigs;

    @Override
    protected int getContentId() {
        return R.layout.fragment_qingyang_asn_detail;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        mConfigs = new ArrayList<>();
    }

    @Override
    protected void initView() {
        //隐藏上架仓位和批次
        tvBatchFlag.setVisibility(View.GONE);
        tvLocation.setVisibility(View.GONE);

        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager lm = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(lm);
        mRecycleView.setItemAnimator(new FadeInDownAnimator());

        //获取配置信息(注意明细界面的额外字段不能每一次页面可见去获取)
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType, Global.DETAIL_PARENT_NODE_CONFIG_TYPE, Global.DETAIL_CHILD_NODE_CONFIG_TYPE);
    }

    @Override
    public void initDataLazily() {
        if (mRefData == null) {
            setRefreshing(false, "获取明细失败,请现在抬头界面选择相应的参数");
            return;
        }

        if (TextUtils.isEmpty(mRefData.workId)) {
            setRefreshing(false, "获取明细失败,请先选择工厂");
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
        String transferFlag = (String) getData(mBizType + mRefType, "0");
        if ("1".equals(transferFlag)) {
            showMessage("本次采集已经过账,请先进行数据上传操作");
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
    public void showNodes(List<RefDetailEntity> nodes) {
        for (RefDetailEntity node : nodes) {
            if(!TextUtils.isEmpty(node.transId)) {
                mTransId = node.transId;
                break;
            }
        }
        ASNDetailAdapter adapter = new ASNDetailAdapter(mActivity, R.layout.item_qingyang_asn_detail, nodes, mConfigs,
                null, mCompanyCode);
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

    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        String state = (String) getData(mBizType + mRefType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许删除");
            return;
        }
        mPresenter.deleteNode("N", node.transId, node.transLineId, node.locationId, mRefData.refType,
                mRefData.bizType, position, mCompanyCode);
    }

    /**
     * 修改明细里面的子节点
     *
     * @param node
     * @param position
     */
    @Override
    public void editNode(final RefDetailEntity node, int position) {
        String state = (String) getData(mBizType + mRefType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许修改");
            return;
        }
        //获取与该子节点的物料编码和发出库位一致的发出仓位和接收仓位列表
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && ASNDetailAdapter.class.isInstance(adapter)) {
            ASNDetailAdapter asnDetailAdapter = (ASNDetailAdapter) adapter;
            ArrayList<String> Locations = asnDetailAdapter.getSendLocations(node.materialNum, node.invId, position);
            mPresenter.editNode(Locations, null, node, mCompanyCode, mBizType, mRefType, "其他入库-无参考");
        }
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && ASNDetailAdapter.class.isInstance(adapter)) {
            ASNDetailAdapter asnDetailAdapter = (ASNDetailAdapter) adapter;
            asnDetailAdapter.removeItemByPosition(position);
            int itemCount = asnDetailAdapter.getItemCount();
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
        BottomMenuAdapter adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu,
                provideDefaultBottomMenu());
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
                    submit2BarcodeSystem();
                    break;
                case 1:
                    //2.数据上传
                    submit2SAP();
                    dialog.dismiss();
                    break;
            }
            dialog.dismiss();
        });
    }

    /**
     * 1.过账
     */
    private void submit2BarcodeSystem() {
        String transferFlag = (String) getData(mBizType, "0");
        if ("1".equals(transferFlag)) {
            showMessage("本次采集已经过账,请先进行数据上传操作");
            return;
        }
        mPresenter.submitData2BarcodeSystem(mTransId, mBizType, mRefType, mRefData.voucherDate);
    }

    @Override
    public void showTransferedVisa(String visa) {
        mTransNum = visa;
    }

    @Override
    public void submitBarcodeSystemSuccess() {
        showSuccessDialog("过账成功" + mTransNum);
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        showMessage(message);
    }

    /**
     * 2.数据上传
     */
    private void submit2SAP() {
        if (TextUtils.isEmpty(mTransNum)) {
            showMessage("请先过账");
            return;
        }
        mPresenter.submitData2SAP(mTransId, mBizType, mRefType, Global.USER_ID,
                mRefData.voucherDate,null, createExtraHeaderMap());
    }

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void submitSAPFail(String[] messages) {
        MainActivity activity = (MainActivity) mActivity;
        FragmentManager fm = activity.getSupportFragmentManager();
        ShowErrorMessageDialog dialog = ShowErrorMessageDialog.newInstance(messages);
        dialog.show(fm, "nms_show_error_messages");
    }

    @Override
    public void networkConnectError(String retryAction) {

    }

}
