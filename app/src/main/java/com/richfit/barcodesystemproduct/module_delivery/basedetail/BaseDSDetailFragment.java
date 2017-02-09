package com.richfit.barcodesystemproduct.module_delivery.basedetail;

import android.app.Dialog;
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
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.adapter.DSYDetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module.main.MainActivity;
import com.richfit.barcodesystemproduct.module_delivery.basedetail.imp.DSDetailPresenterImp;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.dialog.ShowErrorMessageDialog;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by monday on 2016/11/20.
 */

public abstract class BaseDSDetailFragment extends BaseFragment<DSDetailPresenterImp, RefDetailEntity>
        implements IDSDetailView, SwipeRefreshLayout.OnRefreshListener {

    private static final String[] MENUS_NAMES = {"过账", "数据上传"};

    private static final int[] MENUS_IMAGES = {R.mipmap.icon_transfer,
            R.mipmap.icon_data_submit};

    @BindView(R.id.actQuantity)
    protected TextView actQuantityName;

    @BindView(R.id.data_details_recycle_view)
    protected RecyclerView mRecycleView;

    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;

    @BindView(R.id.root_id)
    LinearLayout mExtraContainer;

    String mVisa;

    protected String mTransId;

    @Override
    protected int getContentId() {
        return R.layout.fragment_base_ds_detail;
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
            showMessage("请现在抬头界面获取单据数据");
            return;
        }

        if (isEmpty(mRefData.recordNum)) {
            showMessage("请现在抬头界面输入预留单号");
            return;
        }

        if (isEmpty(mRefData.bizType)) {
            showMessage("未获取到业务类型");
            return;
        }

        if (isEmpty(mRefData.refType)) {
            showMessage("未获取到单据类型");
            return;
        }


        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
            return;
        }

        String transferFlag = (String) SPrefUtil.getData(mBizType + mRefType, "0");
        if ("1".equals(transferFlag)) {
            showMessage("本次采集已经过账,请先进行数据上传操作");
            return;
        }
        startAutoRefresh();
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.parentNodeConfigs = configs.get(0);
        mSubFunEntity.childNodeConfigs = configs.get(1);
        createExtraUI(mSubFunEntity.parentNodeConfigs, EXTRA_HORIZONTAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.parentNodeConfigs = null;
        mSubFunEntity.childNodeConfigs = null;
    }

    @Override
    public void readConfigsComplete() {

    }

    @Override
    public void startAutoRefresh() {
        mSwipeRefreshLayout.postDelayed((() -> {
            mHorizontalScroll.scrollTo((int) (mExtraContainer.getWidth() / 2.0f - UiUtil.getScreenWidth(mActivity) / 2.0f), 0);
            mSwipeRefreshLayout.autoRefresh();
        }), 50);
    }

    /**
     * 自动下拉刷新
     */
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
        //获取缓存累计数量不对
        mPresenter.getTransferInfo(mRefData, refCodeId, bizType, refType);
    }

    /**
     * 如果不是标准的出库，需要重写该方法。
     */
    @Override
    public void showNodes(List<RefDetailEntity> allNodes) {
        for (RefDetailEntity node : allNodes) {
            if (!TextUtils.isEmpty(node.transId)) {
                mTransId = node.transId;
                break;
            }
        }
        DSYDetailAdapter adapter = new DSYDetailAdapter(mActivity, allNodes,
                mSubFunEntity.parentNodeConfigs,mSubFunEntity.childNodeConfigs,
                mCompanyCode);
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
     * 修改明细里面的子节点
     *
     * @param node
     * @param position
     */
    @Override
    public void editNode(final RefDetailEntity node, int position) {
        if (!TextUtils.isEmpty(mVisa)) {
            showMessage("本次入库已经过账,不允许在进行修改");
            return;
        }
        mPresenter.editNode(mRefData, node, mCompanyCode, mBizType, mRefType,
                getSubFunName());
    }

    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        if (!TextUtils.isEmpty(mVisa)) {
            showMessage("本次入库已经过账,不允许在进行删除");
            return;
        }
        TreeNode parentNode = node.getParent();
        String lineDeleteFlag;
        if (parentNode == null) {
            lineDeleteFlag = "N";
        } else {
            lineDeleteFlag = parentNode.getChildren().size() > 1 ? "N" : "Y";
        }

        mPresenter.deleteNode(lineDeleteFlag, node.transId, node.transLineId,
                node.locationId, mRefData.refType, mRefData.bizType, position,
                mCompanyCode);
    }

    /**
     * 如果不是标准的出库，需要重写该方法。
     */
    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && DSYDetailAdapter.class.isInstance(adapter)) {
            DSYDetailAdapter dsyDetailAdapter = (DSYDetailAdapter) adapter;
            dsyDetailAdapter.removeNodeByPosition(position);
        }
    }


    @Override
    public void deleteNodeFail(String message) {
        showMessage("删除失败;" + message);
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
            dialog.dismiss();
        });
    }

    /**
     * 1.过账
     */
    private void submit2BarcodeSystem() {
        String transferFlag = (String) SPrefUtil.getData(mBizType + mRefType, "0");
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
        SPrefUtil.saveData(mBizType + mRefType, "1");
    }

    private void showDialog(String message) {
        new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("温馨提示")
                .setContentText("过账成功;过账凭证" + message)
                .show();
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

    /**
     * 如果不是标准的出库，需要重写该方法。
     */
    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        SPrefUtil.saveData(mBizType + mRefType, "0");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && DSYDetailAdapter.class.isInstance(adapter)) {
            DSYDetailAdapter detailAdapter = (DSYDetailAdapter) adapter;
            detailAdapter.removeAllVisibleNodes();
        }
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


}
