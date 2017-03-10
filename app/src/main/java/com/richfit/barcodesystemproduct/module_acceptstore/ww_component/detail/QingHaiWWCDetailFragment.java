package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingHaiWWCAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

import butterknife.BindView;

import static com.richfit.common_lib.utils.SPrefUtil.getData;

/**
 * 青海委外出库组件明细界面
 * Created by monday on 2017/3/10.
 */

public class QingHaiWWCDetailFragment extends BaseFragment<QingHaiWWCDetailPresenterImp, RefDetailEntity>
        implements QingHaiWWCDetailContract.IQingHaiWWCDetailView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.data_details_recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.swipe_refresh_layout)
    AutoSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView mHorizontalScroll;

    @BindView(R.id.root_id)
    LinearLayout mExtraContainer;

    String mTransNum;
    String mTransId;
    //第二步过账成功后返回的验收单号
    String mInspectionNum;
    QingHaiWWCAdapter mAdapter;
    //当前委外入库真正操作的行号，该行号用来获取对应的行明细
    String mSelectedRefLineNum;


    @Override

    protected int getContentId() {
        return R.layout.fragment_qinghai_wwc_detail;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mRefDetail = null;
        if (bundle != null) {
            mSelectedRefLineNum = bundle.getString(Global.EXTRA_REF_LINE_NUM_KEY);
        }
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
    }

    @Override
    public void initData() {
        if (mRefData == null) {
            showMessage("请现在抬头界面获取单据数据");
            return;
        }

        if (TextUtils.isEmpty(mRefData.recordNum)) {
            showMessage("请现在抬头界面输入验收清单号");
            return;
        }

        if (TextUtils.isEmpty(mBizType)) {
            showMessage("未获取到业务类型");
            return;
        }

        if (TextUtils.isEmpty(mRefType)) {
            showMessage("未获取到单据类型");
            return;
        }

        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("未获取到明细行行号");
            return;
        }
        startAutoRefresh();
    }

    @Override
    public void initDataLazily() {
        initData();
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
            setRefreshing(false, "本次采集已经过账,请先进行数据上传操作");
            return;
        }
        //单据抬头id
        final String refCodeId = mRefData.refCodeId;
        //清除缓存id
        mTransId = "";
        //清除过账凭证
        mTransNum = "";
        //获取缓存累计数量不对
        mRefDetail = null;
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        if (lineData != null) {
            mPresenter.getTransferInfo(mRefData.recordNum, refCodeId, mBizType, mRefType, "", lineData.refLineId, Global.USER_ID);
        }
    }

    @Override
    public void showNodes(List<RefDetailEntity> allNodes) {
        for (RefDetailEntity node : allNodes) {
            if (!TextUtils.isEmpty(node.transId)) {
                mTransId = node.transId;
                break;
            }
        }
        mRefDetail = allNodes;
        if (mAdapter == null) {
            mAdapter = new QingHaiWWCAdapter(mActivity, R.layout.item_qinghai_wwc_detail,
                    allNodes, mSubFunEntity.parentNodeConfigs, mSubFunEntity.childNodeConfigs, mCompanyCode);
            mRecycleView.setAdapter(mAdapter);
            mAdapter.setOnItemEditAndDeleteListener(this);
        } else {
            mAdapter.addAll(allNodes);
        }
    }

    @Override
    public void setRefreshing(boolean isSuccess, String message) {
        //不论成功或者失败都应该关闭下拉加载动画
        mSwipeRefreshLayout.setRefreshing(false);
        showMessage(message);
    }


}
