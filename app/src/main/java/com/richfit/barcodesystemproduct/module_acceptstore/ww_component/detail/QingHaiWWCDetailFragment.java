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
import com.richfit.common_lib.utils.JsonUtil;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.AutoSwipeRefreshLayout;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.richfit.common_lib.utils.SPrefUtil.getData;

/**
 * 青海委外出库组件明细界面
 * Created by monday on 2017/3/10.
 */

public class QingHaiWWCDetailFragment extends BaseFragment<QingHaiWWCDetailPresenterImp, RefDetailEntity>
        implements QingHaiWWCDetailContract.IQingHaiWWCDetailView, SwipeRefreshLayout.OnRefreshListener {

    private static final HashMap<String, Object> FLAGMAP = new HashMap<>();

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
    List<BottomMenuEntity> mBottomMenus;

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
        mPresenter.editNode(null, mRefData, node, mCompanyCode, mBizType,
                mRefType, "委外入库组件", position);
    }

    @Override
    public void deleteNode(final RefDetailEntity node, int position) {
        String state = (String) getData(mBizType + mRefType, "0");
        if (!"0".equals(state)) {
            showMessage("已经过账,不允许修改");
            return;
        }
        if (TextUtils.isEmpty(node.transLineId)) {
            showMessage("该行还未进行数据采集");
            return;
        }
        mPresenter.deleteNode("N", node.transId, node.transLineId,
                node.locationId, mRefData.refType, mRefData.bizType, position, mCompanyCode);
    }

    @Override
    public void deleteNodeFail(String message) {
        showMessage("删除失败;" + message);
    }

    /**
     * 注意该业务相当于有参考，但是没有父子节点结构的明细删除逻辑
     * @param position：节点在明细列表的位置
     */
    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        if (mAdapter != null) {
            mAdapter.removeNodeByPosition(position);
        }
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

//    /**
//     * 显示过账，数据上传等菜单对话框
//     *
//     * @param companyCode
//     */
//    @Override
//    public void showOperationMenuOnDetail(final String companyCode) {
//        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
//        GridView menu = (GridView) rootView.findViewById(R.id.gridview);
//        mBottomMenus = provideDefaultBottomMenu();
//        BottomMenuAdapter adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu, mBottomMenus);
//        menu.setAdapter(adapter);
//
//        final Dialog dialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
//        dialog.setContentView(rootView);
//        dialog.setCancelable(true);
//        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.show();
//
//        menu.setOnItemClickListener((adapterView, view, position, id) -> {
//            switch (position) {
//                case 0:
//                    //1.过账
//                    submit2BarcodeSystem(mBottomMenus.get(position).transToSapFlag);
//                    break;
//                case 1:
//                    submit2SAP(mBottomMenus.get(position).transToSapFlag);
//                    break;
//                case 3:
//                    //转储
////                    sapUpAndDownLocation(mBottomMenus.get(position).transToSapFlag);
//                    break;
//            }
//            dialog.dismiss();
//        });
//    }

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


    @Override
    public void submitBarcodeSystemSuccess() {
        showSuccessDialog(mTransNum);
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        if (TextUtils.isEmpty(message)) {
            message += "过账失败";
        }
        showErrorDialog(message);
        mTransNum = "";
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

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
        if (mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
    }

    @Override
    public void submitSAPFail(String[] messages) {
        showErrorDialog(messages);
        mInspectionNum = "";
    }

    @Override
    public void upAndDownLocationFail(String[] messages) {

    }

    @Override
    public void upAndDownLocationSuccess() {

    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = super.provideDefaultBottomMenu();
        menus.get(0).transToSapFlag = "Z02";
        menus.get(1).transToSapFlag = "Z03";
        return menus.subList(0, 2);
    }

    @Override
    public boolean isNeedShowFloatingButton() {
        return false;
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
