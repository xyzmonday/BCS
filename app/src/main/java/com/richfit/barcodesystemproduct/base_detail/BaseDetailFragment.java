package com.richfit.barcodesystemproduct.base_detail;

import android.app.Dialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 所有明细界面公共基类。注意因为明细界面的控件已经固定，所以可以抽象出来
 * Created by monday on 2017/3/17.
 */

public abstract class BaseDetailFragment<P extends IBaseDetailPresenter, T extends TreeNode> extends
        BaseFragment<P, T> implements IBaseDetailView, SwipeRefreshLayout.OnRefreshListener {

    /*明细界面的公共组件*/
    @BindView(R.id.base_detail_recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.base_detail_swipe_refresh_layout)
    protected AutoSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.base_detail_horizontal_scroll)
    protected HorizontalScrollView mHorizontalScroll;
    @BindView(R.id.root_id)
    protected LinearLayout mExtraContainer;

    /*明细界面的公共适配器，给出父类，所以尽量将方法抽象在父类里面*/
    protected CommonTreeAdapter<T> mAdapter;

    /*过账和数据上传的公共字段和信息*/
    /*第一步过账成功后返回的物料凭证*/
    protected String mTransNum;
    /*缓存的抬头transId,用于删除缓存*/
    protected String mTransId;
    /*第二步(上架，下架，转储)成功后返回的验收单号*/
    protected String mInspectionNum;
    /*底部提示菜单数据源*/
    protected List<BottomMenuEntity> mBottomMenus;
    /*上传数据需要的而外字段集合。*/
    protected  HashMap<String, Object> mFlagMap = new HashMap<>();

    /**
     * 初始化公共的组件，这里统一设置RecyclerView的基本配置；自动下拉刷新接口
     */
    @Override
    protected void initView() {
        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue_a700, R.color.red_a400,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager lm = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
        //获取配置信息(注意明细界面的额外字段不能每一次页面可见去获取)
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType, Global.DETAIL_PARENT_NODE_CONFIG_TYPE,
                Global.DETAIL_CHILD_NODE_CONFIG_TYPE);
    }

    /**
     * 当前明细界面的配置信息成功，那么绑定在rootId的容器上
     *
     * @param configs：配置信息列表，位置索引按照传入的configType的顺序分别保存配置信息。
     */
    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.parentNodeConfigs = configs.get(0);
        mSubFunEntity.childNodeConfigs = configs.get(1);
        createExtraUI(mSubFunEntity.parentNodeConfigs, EXTRA_HORIZONTAL_ORIENTATION_TYPE);
    }

    /**
     * 读取配置信息失败，清除配置信息
     *
     * @param message
     */
    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.parentNodeConfigs = null;
        mSubFunEntity.childNodeConfigs = null;
    }

    /**
     * 这里将整个页面居中的目的是让用户看到下拉刷新动画，并且提示用户正在刷新明细数据。
     */
    @Override
    public void startAutoRefresh() {
        mSwipeRefreshLayout.postDelayed((() -> {
            mHorizontalScroll.scrollTo((int) (mExtraContainer.getWidth() / 2.0f - UiUtil.getScreenWidth(mActivity) / 2.0f), 0);
            mSwipeRefreshLayout.autoRefresh();
        }), 50);
    }

    /**
     * 响应自动下拉刷新动作，并且开始请求接口获取整单缓存
     */
    @Override
    public void onRefresh() {
        if (getTransferState()) {
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
        //获取缓存累计数量不对
        mPresenter.getTransferInfo(mRefData, refCodeId, bizType, refType);
    }

    /**
     * 关闭下拉刷新动画。提示是否获取单整单缓存的信息
     *
     * @param isSuccess
     * @param message
     */
    @Override
    public void setRefreshing(boolean isSuccess, String message) {
        //不论成功或者失败都应该关闭下拉加载动画
        mSwipeRefreshLayout.setRefreshing(false);
        showMessage(message);
    }

    /**
     * 删除某条明细失败。
     *
     * @param message
     */
    @Override
    public void deleteNodeFail(String message) {
        showMessage("删除失败;" + message);
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
     * 第一步成功后记录物料凭证，以便显示
     *
     * @param message
     */
    @Override
    public void showTransferedVisa(String message) {
        mTransNum = message;
    }

    /**
     * 第二步成功后记录过账凭证，以便显示
     *
     * @param inspectionNum
     */
    @Override
    public void showInspectionNum(String inspectionNum) {
        mInspectionNum = inspectionNum;
    }

    /**
     * 当明细界面需要至少两步来上传数据时，需要根据不同的业务需求返回是否已经过账。
     *
     * @return
     */
    protected abstract boolean getTransferState();

    /**
     * 第一步将数据上传到条码系统
     *
     * @param tranToSapFlag
     */
    protected abstract void submit2BarcodeSystem(String tranToSapFlag);

    /**
     * 第二步将数据上传到SAP系统
     *
     * @param tranToSapFlag
     */
    protected abstract void submit2SAP(String tranToSapFlag);

    /**
     * 第三步上架和下架
     *
     * @param transToSapFlag
     */
    protected abstract void sapUpAndDownLocation(String transToSapFlag);
}
