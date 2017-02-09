package com.richfit.barcodesystemproduct.module_locationadjust.header;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Spinner;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.InvAdapter;
import com.richfit.barcodesystemproduct.adapter.WorkAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_locationadjust.header.imp.LAHeaderPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 仓位调整抬头界面
 * Created by monday on 2017/2/7.
 */

public class LAHeaderFragment extends BaseFragment<LAHeaderPresenterImp, Object>
        implements ILAHeaderView {

    @BindView(R.id.sp_work)
    Spinner spWork;
    @BindView(R.id.sp_inv)
    Spinner spInv;

    List<WorkEntity> mWorks;
    List<InvEntity> mInvs;

    WorkAdapter mWorkAdapter;
    InvAdapter mInvAdapter;

    @Override
    protected int getContentId() {
        return R.layout.fragment_la_header;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        super.initVariable(savedInstanceState);
        mRefData = null;
        mSubFunEntity.headerConfigs = null;
        mSubFunEntity.parentNodeConfigs = null;
        mSubFunEntity.childNodeConfigs = null;
        mSubFunEntity.collectionConfigs = null;
        mSubFunEntity.locationConfigs = null;

        mWorks = new ArrayList<>();
        mInvs = new ArrayList<>();
    }

    @Override
    public void initEvent() {
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType, Global.HEADER_CONFIG_TYPE);

        RxAdapterView.itemSelections(spWork)
                .filter(position -> position.intValue() > 0)
                .subscribe(position -> mPresenter.getInvsByWorkId(mWorks.get(position.intValue()).workId,0));

        RxAdapterView.itemSelections(spInv)
                .filter(position -> position.intValue() > 0)
                .subscribe(a -> mPresenter.getStorageNum(mWorks.get(spWork.getSelectedItemPosition()).workId,
                        mWorks.get(spWork.getSelectedItemPosition()).workCode,
                        mInvs.get(spInv.getSelectedItemPosition()).invId,
                        mInvs.get(spInv.getSelectedItemPosition()).invCode));
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.headerConfigs = configs.get(0);
        createExtraUI(mSubFunEntity.headerConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.headerConfigs = null;
    }

    @Override
    public void readConfigsComplete() {
        //获取发出工厂列表
        mPresenter.getWorks(0);
    }

    @Override
    public void showWorks(List<WorkEntity> works) {
        mWorks.clear();
        mWorks.addAll(works);
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
    public void showInvs(List<InvEntity> invs) {
        mInvs.clear();
        mInvs.addAll(invs);
        if (mInvAdapter == null) {
            mInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mInvs);
            spInv.setAdapter(mInvAdapter);
        } else {
            mInvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadInvsFail(String message) {
        showMessage(message);
    }

    @Override
    public void getStorageNumSuccess(String storageNum) {
        if (mRefData == null)
            mRefData = new ReferenceEntity();
        mRefData.storageNum = storageNum;
    }

    @Override
    public void getStorageNumFail(String message) {
        showMessage(message);
    }

    @Override
    public void _onPause() {
        if (checkData()) {
            if (mRefData == null)
                mRefData = new ReferenceEntity();

            if (mWorks != null && mWorks.size() > 0 && spWork.getAdapter() != null) {
                final int position = spWork.getSelectedItemPosition();
                mRefData.workCode = mWorks.get(position).workCode;
                mRefData.workName = mWorks.get(position).workName;
                mRefData.workId = mWorks.get(position).workId;
            }

            if (mInvs != null && mInvs.size() > 0 && spInv.getAdapter() != null) {
                final int position = spInv.getSelectedItemPosition();
                mRefData.invCode = mInvs.get(position).invCode;
                mRefData.invName = mInvs.get(position).invName;
                mRefData.invId = mInvs.get(position).invId;
            }

            mRefData.bizType = mBizType;

            //保存额外字段
            Map<String, Object> extraHeaderMap = saveExtraUIData(mSubFunEntity.headerConfigs);
            mRefData.mapExt = UiUtil.copyMap(extraHeaderMap, mRefData.mapExt);

        } else {
            mRefData = null;
        }
    }

    protected boolean checkData() {
        //检查是否填写了必要的字段
        if (spWork.getSelectedItemPosition() == 0)
            return false;
        if (spInv.getSelectedItemPosition() == 0)
            return false;
        return true;
    }

    @Override
    public void clearAllUI() {
        spWork.setSelection(0);
        spInv.setSelection(0);
        clearExtraUI(mSubFunEntity.headerConfigs);
    }

    @Override
    public void networkConnectError(String retryAction) {

    }

}
