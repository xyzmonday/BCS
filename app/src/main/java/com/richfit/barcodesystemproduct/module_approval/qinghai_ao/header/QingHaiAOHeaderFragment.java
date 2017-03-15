package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.header;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.header.imp.QingHaiAOHeaderPresenterImp;
import com.richfit.common_lib.utils.DateChooseHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by monday on 2017/2/28.
 */

public class QingHaiAOHeaderFragment extends BaseFragment<QingHaiAOHeaderPresenterImp, Object>
        implements IQingHaiAOHeaderView {

    @BindView(R.id.et_ref_num)
    RichEditText etRefNum;
    @BindView(R.id.tv_ref_num)
    TextView tvRefNum;
    @BindView(R.id.tv_supplier)
    TextView tvSupplier;
    @BindView(R.id.et_transfer_date)
    RichEditText etTransferDate;
    @BindView(R.id.tv_creator)
    TextView tvCreator;

    @Override
    public void handleBarCodeScanResult(String type, String[] list) {
        if (list != null && list.length >= 1) {
            getRefData(list[0]);
        }
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected int getContentId() {
        return R.layout.fragment_qinghai_ao_header;
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        mRefData = null;
        mSubFunEntity.headerConfigs = null;
        mSubFunEntity.parentNodeConfigs = null;
        mSubFunEntity.childNodeConfigs = null;
        mSubFunEntity.collectionConfigs = null;
        mSubFunEntity.locationConfigs = null;
    }

    @Override
    protected void initView() {
        etTransferDate.setText(UiUtil.getCurrentDate(Global.GLOBAL_DATE_PATTERN_TYPE1));
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType, Global.HEADER_CONFIG_TYPE);
    }

    /**
     * 绑定事件。
     */
    @Override
    public void initEvent() {
        //请求单据信息
        etRefNum.setOnRichEditTouchListener((view, refNum) -> {
            hideKeyboard(view);
            getRefData(refNum);
        });

        //修改验收日期
        etTransferDate.setOnRichEditTouchListener((view, text) ->
                DateChooseHelper.chooseDateForEditText(mActivity, etTransferDate, Global.GLOBAL_DATE_PATTERN_TYPE1));
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> extraHeaderConfigs) {
        mSubFunEntity.headerConfigs = extraHeaderConfigs.get(0);
        createExtraUI(mSubFunEntity.headerConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.headerConfigs = null;
    }

    protected void getRefData(String refNum) {
        mRefData = null;
        clearAllUI();
        mPresenter.getReference(refNum, mRefType, mBizType, "", Global.USER_ID);
    }

    @Override
    public void getReferenceSuccess(ReferenceEntity refData) {
        //过账标识，如果已经过账，那么不允许在明细刷新数据，也不运行在采集界面采集数据
        SPrefUtil.saveData(mBizType + mRefType, "0");
        refData.bizType = mBizType;//"01"
        refData.refType = mRefType;
        mRefData = refData;
        cacheProcessor(mRefData.tempFlag, mRefData.transId, mRefData.refCodeId, mRefData.recordNum,
                mRefData.refType, mRefData.bizType);
    }

    @Override
    public void getReferenceFail(String message) {
        showMessage(message);
        mRefData = null;
        //清除所有控件绑定的数据
        clearAllUI();
    }

    /**
     * 检查数据库是否存在该历史数据.
     * 如果有缓存提示用户是否删除缓存。
     * 如果用户点击确定删除那么删除缓存，并刷新界面；
     * 如果用户点击取消删除那么直接刷新界面，在采集界面和明细界面会重新获取缓存
     *
     * @param cacheFlag：缓存标志。有可能是Y,N或者TransId标识
     * @param transId：缓存id,用于删除缓存
     * @param refNum：单据号
     * @param bizType：业务类型
     */
    @Override
    public void cacheProcessor(String cacheFlag, String transId, String refCodeId, String refNum, String refType, String bizType) {
        if (!TextUtils.isEmpty(cacheFlag) && "Y".equals(cacheFlag)) {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mActivity);
            dialog.setTitle("提示");
            dialog.setIcon(R.mipmap.icon_tips);
            dialog.setMessage(getString(R.string.has_history));
            dialog.setPositiveButton("确定", (dia, which) -> {
                dia.dismiss();
                mPresenter.deleteCollectionData(refNum, transId, refCodeId, refType, bizType, Global.USER_ID, mCompanyCode);
            });
            dialog.setNegativeButton("取消", (dia, which) -> {
                dia.dismiss();
                bindCommonHeaderUI();
            });
            dialog.show();
        } else {
            bindCommonHeaderUI();
        }
    }

    @Override
    public void deleteCacheSuccess() {
        showMessage("缓存删除成功");
        bindCommonHeaderUI();
    }

    @Override
    public void deleteCacheFail(String message) {
        showMessage(message);
        bindCommonHeaderUI();
    }

    @Override
    public void bindCommonHeaderUI() {
        if (mRefData != null) {
            //单据号
            tvRefNum.setText(mRefData.recordNum);
            //供应商
            tvSupplier.setText(mRefData.supplierNum + "_" + mRefData.supplierDesc);
            //创建人
            tvCreator.setText(Global.LOGIN_ID);
            //绑定额外字段
            bindExtraUI(mSubFunEntity.headerConfigs, mRefData.mapExt);
        }
    }

    @Override
    public void clearAllUI() {
        clearCommonUI(tvRefNum, tvSupplier);
        clearExtraUI(mSubFunEntity.headerConfigs);
    }

    @Override
    public void clearAllUIAfterSubmitSuccess() {
        clearAllUI();
        clearCommonUI(etRefNum);
        mRefData = null;
    }


    @Override
    public void _onPause() {
        //再次检查用户是否输入的额外字段而且必须输入的字段（情景是用户请求单据之前没有输入该字段，回来填上后，但是没有请求单据而是直接）
        //切换了页面
        if (mRefData != null) {
            mRefData.voucherDate = getString(etTransferDate);
            mRefData.bizType = mBizType;
            mRefData.refType = mRefType;
            Map<String, Object> extraHeaderMap = saveExtraUIData(mSubFunEntity.headerConfigs);
            mRefData.mapExt = UiUtil.copyMap(extraHeaderMap, mRefData.mapExt);
        }
    }

    /**
     * 网络错误重试
     *
     * @param action
     */
    @Override
    public void retry(String action) {
        switch (action) {
            case Global.RETRY_LOAD_REFERENCE_ACTION:
                mPresenter.getReference(getString(etRefNum), mRefType, mBizType, "", Global.LOGIN_ID);
                break;
        }
        super.retry(action);
    }
}
