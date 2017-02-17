package com.richfit.barcodesystemproduct.module_acceptstore.basedetail;

import android.app.Dialog;
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
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.richfit.common_lib.utils.SPrefUtil.getData;


/**
 * 物资入库明细界面基类
 * Created by monday on 2016/11/15.
 */

public abstract class BaseASDetailFragment<P extends IASDetailPresenter> extends BaseFragment<P, RefDetailEntity>
        implements IASDetailView, SwipeRefreshLayout.OnRefreshListener {

    private static final HashMap<String, Object> FLAGMAP = new HashMap<>();

    @BindView(R.id.data_details_recycle_view)
    protected RecyclerView mRecycleView;
    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;
    /*是否不上架*/
    @BindView(R.id.y_n_location)
    protected TextView tvIsNLocation;
    /*特殊库存标识*/
    @BindView(R.id.specailInventoryFlag)
    protected TextView tvSpecialInvFag;
    @BindView(R.id.work)
    protected TextView tvWork;
    @BindView(R.id.root_id)
    protected LinearLayout mExtraContainer;
    protected String mTransNum;
    protected String mTransId;
    //第二过账成功后返回的验收单号
    protected String mInspectionNum;
    protected List<BottomMenuEntity> mBottomMenus;

    @Override
    protected int getContentId() {
        return R.layout.fragment_base_as_detail;
    }

    @Override
    protected void initView() {
        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue_a700, R.color.red_a400,
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
    public void initDataLazily() {
        if (mRefData == null) {
            showMessage("请现在抬头界面获取单据数据");
            return;
        }

        if (TextUtils.isEmpty(mRefData.recordNum)) {
            showMessage("请现在抬头界面输入验收清单号");
            return;
        }

        if (TextUtils.isEmpty(mRefData.bizType)) {
            showMessage("未获取到业务类型");
            return;
        }

        if (TextUtils.isEmpty(mRefData.refType)) {
            showMessage("未获取到单据类型");
            return;
        }

        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
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

    /**
     * 自动下拉刷新
     */
    @Override
    public void onRefresh() {
        String transferFlag = (String) getData(mBizType + mRefType, "0");
        if ("1".equals(transferFlag)) {
            setRefreshing(false,"本次采集已经过账,请先进行数据上传操作");
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
        //获取缓存累计数量不对
        mPresenter.getTransferInfo(mRefData, refCodeId, bizType, refType);
    }

    @Override
    public void setRefreshing(boolean isSuccess, String message) {
        //不论成功或者失败都应该关闭下拉加载动画
        mSwipeRefreshLayout.setRefreshing(false);
        showMessage(message);
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
        mPresenter.editNode(null,mRefData, node, mCompanyCode, mBizType,
                mRefType, getSubFunName(),-1);
    }

    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        String state = (String) getData(mBizType + mRefType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许修改");
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
                node.locationId, mRefData.refType, mRefData.bizType, position, mCompanyCode);
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
        String state = (String) SPrefUtil.getData(mBizType + mRefType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账成功,请进行数据上传");
            return;
        }
        mTransNum = "";
        FLAGMAP.clear();
        FLAGMAP.put("transToSapFlag", tranToSapFlag);
        mPresenter.submitData2BarcodeSystem(mTransId, mBizType, mRefType, Global.USER_ID,
                mRefData.voucherDate, FLAGMAP, createExtraHeaderMap());
    }

    @Override
    public void showTransferedVisa(String message) {
        mTransNum = message;
    }

    /**
     * 2.数据上传
     */
    protected void submit2SAP(String tranToSapFlag) {
        String state = (String) getData(mBizType + mRefType, "0");
        if ("0".equals(state)) {
            showMessage("请先过账");
            return;
        }
        FLAGMAP.clear();
        FLAGMAP.put("transToSapFlag", tranToSapFlag);
        mInspectionNum = "";
        mPresenter.submitData2SAP(mTransId, mRefData.bizType, mRefType, Global.USER_ID,
                mRefData.voucherDate, FLAGMAP, createExtraHeaderMap());
    }

    @Override
    public void showInspectionNum(String inspectionNum) {
        mInspectionNum = inspectionNum;
    }

    protected void sapUpAndDownLocation(String tranToSapFlag) {

    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(retryAction);
    }

    /*子类返回修改模块的名称*/
    protected abstract String getSubFunName();
}
