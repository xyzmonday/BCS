package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.header;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxAutoCompleteTextView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.WorkAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.header.imp.QingHaiRSNPresenterImp;
import com.richfit.common_lib.rxutils.RxCilck;
import com.richfit.common_lib.utils.DateChooseHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * Created by monday on 2017/3/2.
 */

public class QingHaiRSNHeaderFragment extends BaseFragment<QingHaiRSNPresenterImp, Object>
        implements IQingHaiRSNHeaderView {

    private static final int ORG_FLAG = 0;

    @BindView(R.id.sp_work)
    Spinner spWork;
    @BindView(R.id.et_cost_center)
    AutoCompleteTextView etCostCenter;
    @BindView(R.id.tv_cost_center_name)
    TextView tvCostCenterName;
    @BindView(R.id.et_transfer_date)
    RichEditText etTransferDate;

    WorkAdapter mWorkAdapter;
    List<WorkEntity> mWorks;

    List<String> mCostCenters;
    ArrayAdapter mCostCenterAdapter;

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_qinghai_rsn_header;
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        mRefData = null;
        mSubFunEntity.headerConfigs = null;
        mSubFunEntity.parentNodeConfigs = null;
        mSubFunEntity.childNodeConfigs = null;
        mSubFunEntity.collectionConfigs = null;
        mSubFunEntity.locationConfigs = null;
        mWorks = new ArrayList<>();
        mCostCenters = new ArrayList<>();
    }

    /**
     * 注册点击事件
     */
    @Override
    public void initEvent() {
        /*选择日期*/
        etTransferDate.setOnRichEditTouchListener((view, text) ->
                DateChooseHelper.chooseDateForEditText(mActivity, etTransferDate, Global.GLOBAL_DATE_PATTERN_TYPE1));

        /*选择工厂，初始化供应商或者项目编号*/
        RxAdapterView.itemSelections(spWork)
                .filter(position -> position.intValue() > 0)
                .subscribe(position -> mPresenter.getAutoCompleteList(mWorks.get(position).workCode,
                        getString(etCostCenter), 100, ORG_FLAG, mBizType));

        RxCilck.clicks(etCostCenter)
                .subscribe(a -> showAutoCompleteConfig(etCostCenter));

        RxTextView.textChanges(etCostCenter)
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(str -> !TextUtils.isEmpty(str) && mCostCenters != null &&
                        mCostCenters.size() > 0 && !filterKeyWord(str) && spWork.getSelectedItemPosition() > 0)
                .subscribe(a -> mPresenter.getAutoCompleteList(mWorks.get(spWork.getSelectedItemPosition()).workCode,
                        getString(etCostCenter), 100, ORG_FLAG, mBizType));

        RxAutoCompleteTextView.itemClickEvents(etCostCenter)
                .subscribe(a -> hideKeyboard(etCostCenter));

        mPresenter.deleteCollectionData("", mBizType, Global.USER_ID, mCompanyCode);
    }

    @Override
    protected void initView() {
        //如果BizType是26那么显示成本中心,否者显示项目编号
        if ("47".equalsIgnoreCase(mBizType)) {
            tvCostCenterName.setText("项目编号");
        }
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType, Global.HEADER_CONFIG_TYPE);
    }

    @Override
    public void initData() {
        SPrefUtil.saveData(mBizType, "0");
        etTransferDate.setText(UiUtil.getCurrentDate(Global.GLOBAL_DATE_PATTERN_TYPE1));
    }

    /**
     * 读取抬头配置文件成功
     *
     * @param configs
     */
    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.headerConfigs = configs.get(0);
        createExtraUI(mSubFunEntity.headerConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    /**
     * 读取抬头配置文件失败
     *
     * @param message
     */
    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.headerConfigs = null;
    }

    @Override
    public void readConfigsComplete() {
        //获取发出工厂列表
        mPresenter.getWorks(ORG_FLAG);
    }

    @Override
    public void showWorks(List<WorkEntity> works) {
        mWorks.clear();
        mWorks.addAll(works);
        //绑定适配器
        if (mWorkAdapter == null) {
            mWorkAdapter = new WorkAdapter(mActivity, R.layout.item_simple_sp, mWorks);
            spWork.setAdapter(mWorkAdapter);
        } else {
            mWorkAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadWorksFail(String message) {
        showMessage(message);
    }

    @Override
    public void showAutoCompleteList(List<String> list) {
        mCostCenters.clear();
        mCostCenters.addAll(list);
        if (mCostCenterAdapter == null) {
            mCostCenterAdapter = new ArrayAdapter<>(mActivity,
                    android.R.layout.simple_dropdown_item_1line, mCostCenters);
            etCostCenter.setAdapter(mCostCenterAdapter);
            setAutoCompleteConfig(etCostCenter);
        } else {
            mCostCenterAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadAutoCompleteFail(String message) {
        hideKeyboard(etCostCenter);
        showMessage(message);
    }

    @Override
    public void deleteCacheSuccess(String message) {
        showMessage(message);
    }

    @Override
    public void deleteCacheFail(String message) {
        showMessage(message);
    }

    private boolean filterKeyWord(CharSequence keyWord) {
        for (String item : mCostCenters) {
            if (item.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void _onPause() {
        super._onPause();
        if (spWork.getAdapter() != null && spWork.getSelectedItemPosition() > 0) {
            if (mRefData == null) {
                mRefData = new ReferenceEntity();
            }
            final int position = spWork.getSelectedItemPosition();
            mRefData.workId = mWorks.get(position).workId;
            mRefData.workCode = mWorks.get(position).workCode;
            mRefData.workName = mWorks.get(position).workName;

            if ("47".equalsIgnoreCase(mBizType)) {
                mRefData.projectNum = getString(etCostCenter).split("_")[0];
            } else {
                mRefData.costCenter = getString(etCostCenter).split("_")[0];
            }
            mRefData.bizType = mBizType;
            mRefData.voucherDate = getString(etTransferDate);

            //保存额外字段
            Map<String, Object> extraHeaderMap = saveExtraUIData(mSubFunEntity.headerConfigs);
            mRefData.mapExt = UiUtil.copyMap(extraHeaderMap, mRefData.mapExt);
        }

    }

    @Override
    public void networkConnectError(String retryAction) {

    }

}
