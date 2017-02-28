package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.MoveTypeAdapter;
import com.richfit.barcodesystemproduct.adapter.MultiArrayAdapter;
import com.richfit.barcodesystemproduct.adapter.WorkAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header.imp.ASNHeaderPresenterImp;
import com.richfit.common_lib.utils.DateChooseHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SupplierEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 无参考物资入库抬头(这里先不在封装成基类)
 * Created by monday on 2016/11/16.
 */

public class QingYangASNHeaderFragment extends BaseFragment<ASNHeaderPresenterImp, Object>
        implements IASNHeaderView {

    private static final String MOVE_TYPE = "5";

    @BindView(R.id.sp_work)
    Spinner spWork;

    @BindView(R.id.sp_move_type)
    Spinner spMoveType;

    @BindView(R.id.et_supplier)
    AutoCompleteTextView etSupplier;

    @BindView(R.id.et_transfer_date)
    RichEditText etTransferDate;

    //工厂类型
    private WorkAdapter mWorkAdapter;
    private MoveTypeAdapter mMoveTypeAdapter;
    private ArrayList<WorkEntity> mWorks;
    private ArrayList<String> mMoveTypes;
    private ArrayList<SupplierEntity> mSuppliers;

    @Override
    protected int getContentId() {
        return R.layout.fragment_qingyang_asn_header;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        mWorks = new ArrayList<>();
        mMoveTypes = new ArrayList<>();
        mSuppliers = new ArrayList<>();
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

    @Override
    public void initEvent() {
        //选择日期
        etTransferDate.setOnRichEditTouchListener((view, text) ->
                DateChooseHelper.chooseDateForEditText(mActivity, etTransferDate, Global.GLOBAL_DATE_PATTERN_TYPE1));

        //选择工厂获取供应商
        RxAdapterView.itemSelections(spWork)
                .filter(position -> position.intValue() > 0)
                .subscribe(position -> mPresenter.getSupplierList(mWorks.get(position.intValue()).workCode,
                        getString(etSupplier),20,0));

        //供应商
        RxView.clicks(etSupplier)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> showAutoCompleteConfig(etSupplier));

        //删除缓存
        mPresenter.deleteCollectionData("", mBizType, Global.USER_ID, mCompanyCode);
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
        //初始化工厂
        mPresenter.getWorks(0);
    }

    @Override
    public void showWorks(ArrayList<WorkEntity> works) {
        if (mWorkAdapter == null) {
            mWorks.clear();
            mWorks.addAll(works);
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
    public void loadWorksComplete() {
        //初始化移动类型
        mPresenter.getMoveTypeList(0);
    }

    @Override
    public void showMoveTypes(ArrayList<String> moveTypes) {
        if (mMoveTypeAdapter == null) {
            mMoveTypes.clear();
            mMoveTypes.addAll(moveTypes);
            mMoveTypeAdapter = new MoveTypeAdapter(mActivity, R.layout.item_simple_sp, mMoveTypes);
            spMoveType.setAdapter(mMoveTypeAdapter);
        } else {
            mMoveTypeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadMoveTypesFail(String message) {
        showMessage(message);
    }

    @Override
    public void showSuppliers(ArrayList<SupplierEntity> suppliers) {
        mSuppliers.clear();
        mSuppliers.addAll(suppliers);
        ArrayList<String> list = new ArrayList<>();
        for (SupplierEntity supplier : suppliers) {
            list.add(supplier.supplierCode + "_" + supplier.supplierName);
        }
        MultiArrayAdapter<String> adapter = new MultiArrayAdapter<>(mActivity,
                R.layout.simple_auto_edittext_item1, list);
        etSupplier.setAdapter(adapter);
        setAutoCompleteConfig(etSupplier);
    }

    @Override
    public void loadSuppliersFail(String message) {
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

    @Override
    public void _onPause() {
        if (checkData()) {
            if (mRefData == null)
                mRefData = new ReferenceEntity();

            //发出工厂(工厂)
            if (mWorks != null && mWorks.size() > 0 && spWork.getAdapter() != null) {
                final int position = spWork.getSelectedItemPosition();
                mRefData.workCode = mWorks.get(position).workCode;
                mRefData.workName = mWorks.get(position).workName;
                mRefData.workId = mWorks.get(position).workId;
            }

            //移动类型
            if (mMoveTypes != null && mMoveTypes.size() > 0 && spMoveType.getAdapter() != null) {
                final int position = spMoveType.getSelectedItemPosition();
                mRefData.moveType = mMoveTypes.get(position);
            }
            //过账日期
            mRefData.voucherDate = getString(etTransferDate);
            //业务类型
            mRefData.bizType = mBizType;
            //移动类型
            mRefData.moveType = MOVE_TYPE;

            final String selectedSupplier = getString(etSupplier);
            if(!TextUtils.isEmpty(selectedSupplier)) {
                final String supplierCode = selectedSupplier.split("_")[0];
                for (SupplierEntity entity : mSuppliers) {
                    if(supplierCode.equalsIgnoreCase(entity.supplierCode)) {
                        mRefData.supplierId = entity.supplierId;
                    }
                }
            }

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
        if (spMoveType.getSelectedItemPosition() == 0)
            return false;
        return true;
    }
}
