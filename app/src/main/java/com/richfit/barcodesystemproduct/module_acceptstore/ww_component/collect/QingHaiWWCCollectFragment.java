package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.collect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by monday on 2017/3/10.
 */

public class QingHaiWWCCollectFragment extends BaseFragment<QingHaiWWCCollectPresenterImp, Object>
        implements QingHaiWWCCollectContract.QingHaiWWCCollectView {


    @BindView(R.id.sp_ref_line_num)
    Spinner spRefLine;
    @BindView(R.id.tv_material_num)
    TextView tvMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_special_inv_flag)
    TextView tvSpecialInvFlag;
    @BindView(R.id.tv_work_name)
    TextView tvWorkName;
    @BindView(R.id.tv_work)
    TextView tvWork;
    @BindView(R.id.act_quantity_name)
    TextView actQuantityName;
    @BindView(R.id.tv_act_quantity)
    TextView tvActQuantity;
    @BindView(R.id.sp_batch_flag)
    Spinner spBatchFlag;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocationQuantity;
    @BindView(R.id.quantity_name)
    TextView quantityName;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.tv_total_quantity)
    TextView tvTotalQuantity;

    String mSelectedRefLineNum;
    ArrayAdapter<String> mRefLineAdapter;
    /*库存信息*/
    private List<InventoryEntity> mInventoryDatas;
    /*批次下拉*/
    private ArrayAdapter<String> mBatchFlagAdapter;

    @Override
    protected int getContentId() {
        return R.layout.fragment_qinghai_wwc_collect;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSelectedRefLineNum = bundle.getString(Global.EXTRA_REF_LINE_NUM_KEY);
        }
    }

    @Override
    public void initEvent() {
           /*单据行*/
        RxAdapterView
                .itemSelections(spRefLine)
                .filter(position -> position > 0)
                .subscribe(position -> bindCommonCollectUI());
    }

    @Override
    public void initDataLazily() {
        if (mRefDetail == null) {
            showMessage("未获取到数据明细");
            return;
        }

        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("未获取到单据行号");
            return;
        }
        setupRefLineAdapter();
    }

    @Override
    public void setupRefLineAdapter() {
        if (mRefLineAdapter != null)
            return;
        ArrayList<String> refLines = new ArrayList<>();
        refLines.add("请选择");
        for (RefDetailEntity item : mRefDetail) {
            refLines.add(String.valueOf(item.refDocItem));
        }

        //初始化单据行适配器
        if (mRefLineAdapter == null) {
            mRefLineAdapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp, refLines);
            spRefLine.setAdapter(mRefLineAdapter);

        } else {
            mRefLineAdapter.notifyDataSetChanged();
        }
        //默认选择第一个
        spRefLine.setSelection(1);
    }

    @Override
    public void bindCommonCollectUI() {
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        tvMaterialNum.setText(lineData.materialNum);
        etQuantity.setText("");
        //物资描述
        tvMaterialDesc.setText(lineData.materialDesc);
        //特殊库存标识
        tvSpecialInvFlag.setText(lineData.specialInvFlag);
        //工厂
        tvWork.setText(lineData.workName);
        //应收数量
        tvActQuantity.setText(lineData.actQuantity);
        //获取库存
        mPresenter.getInventoryInfo("01", lineData.workId,
                "", lineData.workCode, "", "", getString(tvMaterialNum),
                lineData.materialId, "", "", "O", mRefData.supplierNum, "1");
    }

    @Override
    public void showInventory(List<InventoryEntity> list) {
        mInventoryDatas.clear();
        mInventoryDatas.addAll(list);

        ArrayList<String> batchFlags = new ArrayList<>();

        for (InventoryEntity item : list) {
            batchFlags.add(item.batchFlag);
        }

        if (mBatchFlagAdapter == null) {
            mBatchFlagAdapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp, batchFlags);
            spBatchFlag.setAdapter(mBatchFlagAdapter);
        } else {
            mBatchFlagAdapter.notifyDataSetChanged();
        }
        spBatchFlag.setSelection(0);
    }

    @Override
    public void loadInventoryFail(String message) {

    }

    @Override
    public void onBindCache(RefDetailEntity cache, String batchFlag, String location) {

    }

    @Override
    public void loadCacheSuccess() {

    }

    @Override
    public void loadCacheFail(String message) {

    }

}
