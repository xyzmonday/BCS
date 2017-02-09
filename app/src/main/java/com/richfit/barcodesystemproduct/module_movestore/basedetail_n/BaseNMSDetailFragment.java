package com.richfit.barcodesystemproduct.module_movestore.basedetail_n;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module.main.MainActivity;
import com.richfit.barcodesystemproduct.module_movestore.basedetail_n.imp.NMSDetailPresenterImp;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.dialog.ShowErrorMessageDialog;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by monday on 2016/11/20.
 */

public abstract class BaseNMSDetailFragment extends BaseFragment<NMSDetailPresenterImp, RefDetailEntity>
        implements INMSDetailView, SwipeRefreshLayout.OnRefreshListener {

    protected static final String[] MENUS_NAMES = {"过账", "数据上传"};

    private static final int[] MENUS_IMAGES = {R.mipmap.icon_transfer, R.mipmap.icon_data_submit};

    @BindView(R.id.sendInv)
    protected TextView sendInv;

    @BindView(R.id.sendLoc)
    protected TextView sendLoc;

    @BindView(R.id.sendBatchFlag)
    protected TextView sendBatchFlag;

    @BindView(R.id.recLoc)
    protected TextView recLoc;

    @BindView(R.id.recBatchFlag)
    protected TextView recRecBatchFlag;

    @BindView(R.id.data_details_recycle_view)
    protected RecyclerView mRecycleView;

    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;

    @BindView(R.id.root_id)
    protected LinearLayout mExtraContainer;

    protected String mTransId;

    protected String mVisa;

    //父子节点的配置信息结合
    protected ArrayList<RowConfig> mConfigs;

    @Override
    protected int getContentId() {
        return R.layout.fragment_base_nms_detail;
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
            setRefreshing(false, "获取明细失败,请现在抬头界面选择相应的参数");
            return;
        }

        if (TextUtils.isEmpty(mRefData.workId) ||
                TextUtils.isEmpty(mRefData.recWorkId)) {
            setRefreshing(false, "获取明细失败,请先选择发出工厂和接收工厂");
            return;
        }

        if (TextUtils.isEmpty(mRefData.recInvId)) {
            setRefreshing(false, "获取明细失败,请先选择接收库位");
            return;
        }

        if (!checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请先在抬头界面输入必要的信息");
            return;
        }

        String transferKey = (String) SPrefUtil.getData(mBizType, "0");
        if ("1".equals(transferKey)) {
            showMessage("本次采集已经过账,请先到数据明细界面进行数据上传操作");
            mSwipeRefreshLayout.setRefreshing(false);
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
    public void setRefreshing(boolean isSuccess, String message) {
        //不论成功或者失败都应该关闭下拉加载动画
        mSwipeRefreshLayout.setRefreshing(false);
        mExtraContainer.setVisibility(isSuccess ? View.VISIBLE : View.INVISIBLE);
        showMessage(message);
    }

    @Override
    public void onRefresh() {
        //单据抬头id
        final String refCodeId = mRefData.refCodeId;
        //业务类型
        final String bizType = mRefData.bizType;
        //单据类型
        final String refType = mRefData.refType;
        //清除缓存id
        mTransId = "";
        //清除过账凭证
        mVisa = "";
        //获取缓存
        mPresenter.getTransferInfo(refCodeId, bizType, refType, Global.USER_ID, mRefData.workId,
                mRefData.invId, mRefData.recWorkId, mRefData.recInvId);
    }

    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        if (!TextUtils.isEmpty(mVisa)) {
            showMessage("本次移库操作已经过账");
            return;
        }
        mPresenter.deleteNode("N", node.transId, node.transLineId, node.locationId,
                mRefData.refType, mRefData.bizType, position, mCompanyCode);
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
                Arrays.asList(MENUS_NAMES), MENUS_IMAGES);
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
        });
    }

    /**
     * 1.过账
     */
    private void submit2BarcodeSystem() {
        String transferFlag = (String) SPrefUtil.getData(mBizType, "0");
        if ("1".equals(transferFlag)) {
            showMessage("本次采集已经过账,请先进行数据上传操作");
            return;
        }
        mPresenter.submitData2BarcodeSystem(mTransId, mRefData.bizType, mRefType, mRefData.voucherDate);
    }

    @Override
    public void showTransferedVisa(String visa) {
        mVisa = visa;
    }

    @Override
    public void submitBarcodeSystemSuccess() {
        showDialog(mVisa);
        SPrefUtil.saveData(mBizType, "1");
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("提示");
        builder.setMessage("过账成功;过账凭证" + message);
        builder.setPositiveButton("确定", (dialog, l) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        showMessage(message);
    }

    /**
     * 2.数据上传
     */
    private void submit2SAP() {
        if (TextUtils.isEmpty(mVisa)) {
            showMessage("请先过账");
            return;
        }
        mPresenter.submitData2SAP(mTransId, mRefData.bizType, mRefType, Global.USER_ID,
                mRefData.voucherDate, createExtraHeaderMap());
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
        showNetConnectErrorDialog(retryAction);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_TRANSFER_DATA_ACTION:
                submit2BarcodeSystem();
                break;
            case Global.RETRY_UPLOAD_DATA_ACTION:
                submit2SAP();
                break;
        }
        super.retry(retryAction);
    }

    /*子类返回修改模块的名称*/
    protected abstract String getSubFunName();

    /**
     * 返回底部菜单标题，这是为了控制有些只需要上传到条码系统，有些
     * 需要需要上传到条码系统和ERP系统
     * @return
     */
    protected abstract List<String> getBottomMenuTitles();

}
