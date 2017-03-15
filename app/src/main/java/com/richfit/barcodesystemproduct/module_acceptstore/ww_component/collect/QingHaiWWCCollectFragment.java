package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.collect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

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
        mInventoryDatas = new ArrayList<>();
    }

    @Override
    public void initEvent() {
        /*单据行*/
        RxAdapterView
                .itemSelections(spRefLine)
                .filter(position -> position > 0)
                .subscribe(position -> bindCommonCollectUI());
        /*选择批次获获取缓存，初始化仓位数量*/
        RxAdapterView.itemSelections(spBatchFlag)
                .filter(position -> position > 0)
                .subscribe(position -> loadLocationQuantity(position.intValue()));
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
        //库存标识
        tvSpecialInvFlag.setText("O");
        //获取库存
        mPresenter.getInventoryInfo("01", lineData.workId,
                "", lineData.workCode, "", "", getString(tvMaterialNum),
                lineData.materialId, "", "", "O", mRefData.supplierNum, "1");
    }

    @Override
    public void showInventory(List<InventoryEntity> list) {
        mInventoryDatas.clear();

        InventoryEntity tmp = new InventoryEntity();
        tmp.batchFlag = "请选择";
        mInventoryDatas.add(tmp);
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
        spBatchFlag.setSelection(1);
    }

    @Override
    public void loadInventoryFail(String message) {
        showMessage(message);
        mInventoryDatas.clear();
        if (mBatchFlagAdapter != null) {
            mBatchFlagAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载单条缓存
     */
    private void loadLocationQuantity(int position) {
        if (position <= 0 || position >= mInventoryDatas.size())
            return;
        if (TextUtils.isEmpty(mRefData.refCodeId)) {
            showMessage("参考单据的Id为空");
            return;
        }
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        if (lineData == null) {
            showMessage("未获取到" + mSelectedRefLineNum + "对应的明细数据");
            return;
        }
        final String refLineId = lineData.refLineId;
        final String batchFlag = mInventoryDatas.get(position - 1).batchFlag;
        mPresenter.getTransferInfoSingle(mRefData.refCodeId, mRefType, mBizType, refLineId, batchFlag, "",
                lineData.refDoc, UiUtil.convertToInt(lineData.refDocItem), Global.USER_ID);
    }

    /**
     * 通过批次匹配出缓存的仓位数量。
     */
    @Override
    public void onBindCache(RefDetailEntity cache, String batchFlag, String location) {
        if (cache != null) {
            tvTotalQuantity.setText(cache.totalQuantity);
        }
    }

    @Override
    public void loadCacheSuccess() {
        showMessage("获取缓存成功");
    }

    @Override
    public void loadCacheFail(String message) {
        showMessage(message);
        tvTotalQuantity.setText("0");
    }

    /**
     * 获取行明细(这里获取的是界面得到的mRefDetail)
     *
     * @param lineNum:单据行号
     * @return
     */
    protected RefDetailEntity getLineData(String lineNum) {
        final int index = getIndexByLineNum(lineNum);
        return mRefDetail.get(index);
    }


    protected boolean refreshQuantity(final String quantity) {
        //将已经录入的所有的子节点的仓位数量累加
        final float totalQuantityV = UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        final float actQuantityV = UiUtil.convertToFloat(getString(tvActQuantity), 0.0f);
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);
        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("输入数量不合理");
            return false;
        }
        if (Float.compare(quantityV + totalQuantityV, actQuantityV) > 0.0f) {
            showMessage("输入数量有误，请出现输入");
            return false;
        }
        return true;
    }


    @Override
    public boolean checkCollectedDataBeforeSave() {

        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
            return false;
        }

        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("请先获取物料信息");
            return false;
        }

        //检查数据是否可以保存
        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请先选择单据行");
            return false;
        }

        if (!refreshQuantity(getString(etQuantity))) {
            return false;
        }

        //检查额外字段是否合格
        if (!checkExtraData(mSubFunEntity.collectionConfigs)) {
            showMessage("请检查输入数据");
            return false;
        }

        if (!checkExtraData(mSubFunEntity.locationConfigs)) {
            showMessage("请检查输入数据");
            return false;
        }
        return true;
    }

    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("提示");
        builder.setMessage("您真的需要保存数据吗?点击确定将保存数据.");
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
            saveCollectedData();
        });
        builder.show();
    }

    @Override
    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }
        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            ResultEntity result = new ResultEntity();
            result.businessType = mBizType;
            result.refCodeId = mRefData.refCodeId;
            result.refCode = mRefData.recordNum;
            result.refLineNum = lineData.lineNum;
            result.voucherDate = mRefData.voucherDate;
            result.refType = mRefType;
            result.moveType = mRefData.moveType;
            result.userId = Global.USER_ID;
            result.refLineId = lineData.refLineId;
            result.workId = lineData.workId;
            result.materialId = lineData.materialId;
            result.location = "barcode";
            result.batchFlag = mInventoryDatas.get(spBatchFlag.getSelectedItemPosition()).batchFlag;
            result.quantity = getString(etQuantity);
            result.modifyFlag = "N";
            result.refDoc = lineData.refDoc;
            result.refDocItem = lineData.refDocItem;
            result.supplierNum = mRefData.supplierNum;
            result.specialInvFlag = getString(tvSpecialInvFlag);
            result.specialInvNum = mRefData.supplierNum;
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCollectionDataSingle(result));
    }

    @Override
    public void saveCollectedDataSuccess() {
        showMessage("保存数据成功");
        final float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0f);
        final float totalQuantity = UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        tvTotalQuantity.setText(String.valueOf(totalQuantity + quantityV));
        etQuantity.setText("");
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage("保存数据失败;" + message);
    }

    @Override
    public void _onPause() {
        super._onPause();
        clearCommonUI(tvMaterialNum, tvMaterialNum, tvSpecialInvFlag, tvWork, tvActQuantity, tvTotalQuantity);
        //清除下拉
        if (mRefLineAdapter != null) {
            spRefLine.setSelection(0);
        }
        if (mBatchFlagAdapter != null) {
            mInventoryDatas.clear();
            mBatchFlagAdapter.notifyDataSetChanged();
        }
    }
}
