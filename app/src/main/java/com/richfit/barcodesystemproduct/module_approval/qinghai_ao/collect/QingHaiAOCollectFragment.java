package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.collect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.adapter.InvAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.camera.TakephotoActivity;
import com.richfit.barcodesystemproduct.module_approval.qinghai_ao.collect.imp.QingHaiAOCollectPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.ArithUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by monday on 2017/2/28.
 */

public class QingHaiAOCollectFragment extends BaseFragment<QingHaiAOCollectPresenterImp, Object>
        implements IQingHaiAOCollectView {

    public static final String DEFUALT_CHOOSED_FLAG = "X";

    @BindView(R.id.sp_ref_line_num)
    Spinner spRefLine;
    @BindView(R.id.et_material_num)
    RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_special_inv_flag)
    TextView tvSpecialInvFlag;
    @BindView(R.id.tv_order_quantity)
    TextView tvOrderQuantity;
    @BindView(R.id.act_quantity_name)
    TextView actQuantityName;
    @BindView(R.id.tv_act_quantity)
    TextView tvActQuantity;
    @BindView(R.id.sp_inv)
    Spinner spInv;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.et_manufacturer)
    EditText etManufacturer;
    @BindView(R.id.cb_certificate)
    CheckBox cbCertificate;
    @BindView(R.id.cb_instructions)
    CheckBox cbInstructions;
    @BindView(R.id.cb_qm_certificate)
    CheckBox cbQmCertificate;
    @BindView(R.id.et_sample_quantity)
    EditText etSampleQuantity;
    //    @BindView(R.id.et_sample_ratio)
//    EditText etSampleRatio;
    @BindView(R.id.et_qualified_quantity)
    EditText etQualifiedQuantity;
    @BindView(R.id.et_corrosion)
    EditText etCorrosion;
    @BindView(R.id.et_damage)
    EditText etDamage;
    @BindView(R.id.et_bad)
    EditText etBad;
    @BindView(R.id.et_other_quantity)
    EditText etOtherQuantity;
    @BindView(R.id.sp_package_condition)
    Spinner spPackageCondition;
    @BindView(R.id.sp_inspection_result)
    Spinner spInspectionResult;
    @BindView(R.id.et_inspect_quantity)
    EditText etInspectQuantity;
    @BindView(R.id.et_qm_num)
    EditText etQmNum;
    @BindView(R.id.et_query_claim_num)
    EditText etQueryClaimNum;
    //累计数量
    @BindView(R.id.tv_total_quantity)
    TextView tvTotalQuantity;
    @BindView(R.id.tv_sample_total_quantity)
    TextView tvSampleTotalQuantity;
    @BindView(R.id.tv_qualified_total_quantity)
    TextView tvQualifiedTotalQuantity;
    @BindView(R.id.tv_corrosion_total_quantity)
    TextView tvCorrosionTotalQuantity;
    @BindView(R.id.tv_damage_total_quantity)
    TextView tvDamageTotalQuantity;
    @BindView(R.id.tv_bad_total_quantity)
    TextView tvBadTotalQuantity;
    @BindView(R.id.tv_other_total_quantity)
    TextView tvOtherTotalQuantity;

    /*/匹配的检验批行明细*/
    List<String> mRefLines;
    ArrayAdapter<String> mRefLineAdapter;
    /*当前正在操作的单据行号*/
    String mSelectedRefLineNum;
    /*库存地点*/
    List<InvEntity> mInvDatas;
    InvAdapter mInvAdapter;
    /*缓存行明细*/
    RefDetailEntity mCachedRefDetailData;


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_qinghai_ao_collect;
    }

    @Override
    public void handleBarCodeScanResult(String type, String[] list) {

        if (list != null && list.length >= 12) {
            final String materialNum = list[Global.MATERIAL_POS];
            final String batchFlag = list[Global.BATCHFALG_POS];
            etMaterialNum.setText(materialNum);
            loadMaterialInfo(materialNum, batchFlag);
        }
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        mRefLines = new ArrayList<>();
        mInvDatas = new ArrayList<>();
    }

    @Override
    protected void initView() {
        //读取额外字段配置信息
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.COLLECT_CONFIG_TYPE);
    }

    @Override
    public void initEvent() {
        /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            hideKeyboard(etMaterialNum);
            //手动输入没有批次
            loadMaterialInfo(materialNum, "");
        });

        /*单据行*/
        RxAdapterView
                .itemSelections(spRefLine)
                .filter(position -> position > 0)
                .subscribe(position -> getTransferInfoSingle(position.intValue()));
    }

    @Override
    public void initData() {
        //初始化包装结果以及验收结果
        ArrayAdapter<String> pakageConditionAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_spinner_item, getStringArray(R.array.package_conditions));
        ArrayAdapter<String> inspectionResultAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_spinner_item, getStringArray(R.array.inspection_results));
        spPackageCondition.setAdapter(pakageConditionAdapter);
        spInspectionResult.setAdapter(inspectionResultAdapter);
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.collectionConfigs = configs.get(0);
        createExtraUI(mSubFunEntity.collectionConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.collectionConfigs = null;
    }

    @Override
    public void initDataLazily() {
        etMaterialNum.setEnabled(false);
        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.refType)) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.voucherDate)) {
            showMessage("请先在抬头界面选择过账日期");
            return;
        }
        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
            return;
        }
        etMaterialNum.setEnabled(true);
    }

    /**
     * 加载物料明细信息。
     *
     * @param materialNum:物资条码
     */
    @Override
    public void loadMaterialInfo(final String materialNum, final String batchFlag) {
        if (!etMaterialNum.isEnabled())
            return;

        if (TextUtils.isEmpty(materialNum)) {
            showMessage("请输入物资条码");
            return;
        }
        clearAllUI();

        //刷新界面(在单据行明细查询是否有该物料条码，如果有那么刷新界面)
        matchMaterialInfo(materialNum, batchFlag)
                .compose(TransformerHelper.io2main())
                .subscribe(details -> setupRefLineAdapter(details), e -> showMessage(e.getMessage()));
    }

    /**
     * 设置单据行适配器
     *
     * @param refLines
     */
    @Override
    public void setupRefLineAdapter(ArrayList<String> refLines) {
        mRefLines.clear();
        mRefLines.add(getString(R.string.default_ref_name));
        if (refLines != null)
            mRefLines.addAll(refLines);

        //如果未查询到提示用户
        if (mRefLines.size() == 1) {
            showMessage("该单据中未查询到该物料,请检查物资编码是否正确");
            spRefLine.setSelection(0);
            return;
        }

        //初始化单据行适配器
        if (mRefLineAdapter == null) {
            mRefLineAdapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp, mRefLines);
            spRefLine.setAdapter(mRefLineAdapter);

        } else {
            mRefLineAdapter.notifyDataSetChanged();
        }
        //如果多行设置颜色
        spRefLine.setBackgroundColor(ContextCompat.getColor(mActivity, mRefLines.size() >= 3 ?
                R.color.colorPrimary : R.color.white));

        //默认选择第一个
        spRefLine.setSelection(1);
    }

    /**
     * 获取验收缓存
     *
     * @param index
     */
    private void getTransferInfoSingle(int index) {
        spRefLine.setSelection(index);
        mCachedRefDetailData = null;
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        if (lineData == null)
            return;
        final String refCodeId = mRefData.refCodeId;
        final String refType = mRefData.refType;
        final String bizType = mRefData.bizType;
        final String refLineId = lineData.refLineId;
        mPresenter.getTransferInfoSingle(refCodeId, refType, bizType, refLineId, "", "",
                lineData.refDoc, UiUtil.convertToInt(lineData.refDocItem), Global.USER_ID);
    }

    @Override
    public void onBindCache(RefDetailEntity cache, String batchFlag, String location) {
        if (cache != null) {
            mCachedRefDetailData = cache;
        }
    }

    @Override
    public void loadCacheFail(String message) {
        showMessage(message);
        mCachedRefDetailData = null;
        clearAllUI();
    }

    @Override
    public void bindCommonCollectUI() {
        etQuantity.setText("");
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        //物资描述
        tvMaterialDesc.setText(lineData.materialDesc);
        //特殊库存标识
        tvSpecialInvFlag.setText(lineData.specialInvFlag);
        //单据数量(凭证数量)
        tvOrderQuantity.setText(lineData.orderQuantity);
        //应收数量
        tvActQuantity.setText(lineData.actQuantity);

        //默认
        //实收数量
        etQuantity.setText(lineData.actQuantity);
        //抽检数量
        etSampleQuantity.setText(lineData.actQuantity);
        //完好数量
        etQualifiedQuantity.setText(lineData.actQuantity);

        cbCertificate.setChecked(true);
        cbInstructions.setChecked(true);
        cbQmCertificate.setChecked(true);

        if (mCachedRefDetailData != null) {
            //制造商名称
            etManufacturer.setText(mCachedRefDetailData.manufacturer);
            //实收数量
//            etQuantity.setText(TextUtils.isEmpty(mCachedRefDetailData.totalQuantity) ? lineData.actQuantity : mCachedRefDetailData.totalQuantity);
            tvTotalQuantity.setText(mCachedRefDetailData.totalQuantity);
            //抽检数量
//            etSampleQuantity.setText(TextUtils.isEmpty(mCachedRefDetailData.randomQuantity) ? lineData.actQuantity : mCachedRefDetailData.randomQuantity);
            tvSampleTotalQuantity.setText(mCachedRefDetailData.randomQuantity);
            //完好数量
//            etQualifiedQuantity.setText(TextUtils.isEmpty(mCachedRefDetailData.qualifiedQuantity) ? lineData.actQuantity : mCachedRefDetailData.qualifiedQuantity);
            tvQualifiedTotalQuantity.setText(mCachedRefDetailData.qualifiedQuantity);
            //锈蚀数量
//            etCorrosion.setText(mCachedRefDetailData.rustQuantity);
            tvCorrosionTotalQuantity.setText(mCachedRefDetailData.rustQuantity);
            //损坏
//            etDamage.setText(mCachedRefDetailData.damagedQuantity);
            tvDamageTotalQuantity.setText(mCachedRefDetailData.damagedQuantity);
            //变质
//            etBad.setText(mCachedRefDetailData.badQuantity);
            tvBadTotalQuantity.setText(mCachedRefDetailData.badQuantity);
            //其他
//            etOtherQuantity.setText(mCachedRefDetailData.otherQuantity);
            tvOtherTotalQuantity.setText(mCachedRefDetailData.otherQuantity);
            //包装情况
            setSelectionForSp(getStringArray(R.array.package_conditions), mCachedRefDetailData.sapPackage, spPackageCondition);
            //验收结果
            setSelectionForSp(getStringArray(R.array.inspection_results), mCachedRefDetailData.inspectionResult, spInspectionResult);
            //送检数量
            etInspectQuantity.setText(mCachedRefDetailData.inspectionQuantity);
            // 质检单号
            etQmNum.setText(mCachedRefDetailData.qmNum);
            //索赔单号
            etQueryClaimNum.setText(mCachedRefDetailData.claimNum);
            //说明书
            cbInstructions.setSelected(DEFUALT_CHOOSED_FLAG.equals(mCachedRefDetailData.instructions));
            //合格证
            cbCertificate.setSelected(DEFUALT_CHOOSED_FLAG.equalsIgnoreCase(mCachedRefDetailData.certificate));
            //质检证书
            cbQmCertificate.setSelected(DEFUALT_CHOOSED_FLAG.equals(mCachedRefDetailData.qmCertificate));

            bindExtraUI(mSubFunEntity.collectionConfigs, mCachedRefDetailData.mapExt);
        }
        //获取库存地点
        mPresenter.getInvsByWorkId(lineData.workId, 0);
    }

    private void setSelectionForSp(List<String> dataSet, String keyWord, Spinner sp) {
        int position = -1;
        if (TextUtils.isEmpty(keyWord))
            return;
        if (sp.getAdapter() == null)
            return;
        if (dataSet != null) {
            for (String item : dataSet) {
                position++;
                if (item.equalsIgnoreCase(keyWord)) {
                    break;
                }
            }
        }
        sp.setSelection(position == -1 ? 0 : position);
    }

    @Override
    public void showInvs(List<InvEntity> invs) {
        //初始化库存地点
        mInvDatas.clear();
        mInvDatas.addAll(invs);
        if (mInvAdapter == null) {
            mInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mInvDatas);
            spInv.setAdapter(mInvAdapter);
        } else {
            mInvAdapter.notifyDataSetChanged();
        }
        int position = -1;
        if (mCachedRefDetailData != null) {
            final String cachedInvCode = mCachedRefDetailData.invCode;
            if (!TextUtils.isEmpty(cachedInvCode)) {
                //如果有缓存，那么系统自动选择缓存
                for (InvEntity data : mInvDatas) {
                    position++;
                    if (cachedInvCode.equals(data.invCode)) {
                        break;
                    }
                }
            }
            //默认选择第一个
            spInv.setSelection(position == -1 ? 0 : position);
        }
    }

    @Override
    public void loadInvsFail(String message) {
        showMessage(message);
        SpinnerAdapter adapter = spInv.getAdapter();
        if (adapter != null && InvAdapter.class.isInstance(adapter)) {
            mInvDatas.clear();
            InvAdapter invAdapter = (InvAdapter) adapter;
            invAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 1.实收数量应等于完好数、锈蚀、损坏、变质、其他质量问题数量之和
     *
     * @param quantity:实收数量
     * @param sampleQuantity:抽检数量
     * @return
     */
    private boolean refreshQuantity(final String quantity, final String sampleQuantity) {
        //实收数量
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);

        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("输入实收数量有误,请出现输入");
            etQuantity.setText("");
            return false;
        }
        //抽检数量
        final float sampleQuantityV = UiUtil.convertToFloat(sampleQuantity, 0.0f);
        if (Float.compare(sampleQuantityV, quantityV) > 0.0f) {
            showMessage("抽检数量不能大于实收数量");
            return false;
        }
        //完好数量
        final float qualifiedQuantityV = UiUtil.convertToFloat(getString(etQualifiedQuantity), 0.0f);
        //腐蚀
        final float rustCorrosionQuantityV = UiUtil.convertToFloat(getString(etCorrosion), 0.0f);
        //损坏
        final float damagedQuantityV = UiUtil.convertToFloat(getString(etDamage), 0.0f);
        //变质
        final float badQuantityV = UiUtil.convertToFloat(getString(etBad), 0.0f);
        //其他质量问题
        final float otherQuantityV = UiUtil.convertToFloat(getString(etOtherQuantity), 0.0f);


        //计算这几个数量的和，注意精度问题
        float tmp = ArithUtil.addAll(qualifiedQuantityV, rustCorrosionQuantityV, damagedQuantityV,
                badQuantityV, otherQuantityV);

        //注意这里不能完全判断相等，如果两个数量的精度在10^-3我们认为它们相等(实际测试的结果是10^-7)
        if (Math.abs(ArithUtil.sub(tmp, quantityV)) > 0.0001) {
            showMessage("完好数量,锈蚀,损坏,变质,其他质量问题数量之和不等于实收数量");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {

        if (!etMaterialNum.isEnabled()) {
            showMessage("请先获取单据数据");
            return false;
        }

        //检查数据是否可以保存
        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请选选择单据行");
            return false;
        }
        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("请选选择单据行");
            return false;
        }

//        if (spInv.getSelectedItemPosition() == 0) {
//            showMessage("请选择库存点");
//            return false;
//        }

        if (!refreshQuantity(getString(etQuantity), getString(etSampleQuantity))) {
            return false;
        }

        if (!checkExtraData(mSubFunEntity.collectionConfigs)) {
            showMessage("请先输入必输字段");
            return false;
        }
        return true;
    }

    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
        GridView menu = (GridView) rootView.findViewById(R.id.gridview);
        final List<BottomMenuEntity> menus = provideDefaultBottomMenu();
        BottomMenuAdapter adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu, menus);
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
                    //1.保存数据
                    saveCollectedData();
                    break;
                case 1:
                    //2.拍照
                    toTakePhoto(menus.get(position).menuName, menus.get(position).takePhotoType);
                    break;
            }
            dialog.dismiss();
        });
    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = new ArrayList<>();

        BottomMenuEntity menu = new BottomMenuEntity();
        menu.menuName = "保存数据";
        menu.menuImageRes = R.mipmap.icon_save_data;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "外观拍照";
        menu.menuImageRes = R.mipmap.icon_take_photo4;
        menu.takePhotoType = 4;
        menus.add(menu);
        return menus;
    }

    @Override
    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }
        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            ResultEntity result = new ResultEntity();
            result.refCodeId = mRefData.refCodeId;
            result.refLineId = lineData.refLineId;
            result.businessType = mRefData.bizType;
            result.materialId = lineData.materialId;
            result.refType = mRefData.refType;
            result.moveType = mRefData.moveType;
            result.inspectionType = mRefData.inspectionType;
            result.inspectionPerson = Global.USER_ID;
            result.userId = Global.USER_ID;
            result.invId = mInvDatas.get(spInv.getSelectedItemPosition()).invId;
            //制造商
            result.manufacturer = getString(etManufacturer);
            //实收数量
            result.quantity = getString(etQuantity);
            //抽检数量
            result.randomQuantity = getString(etSampleQuantity);
            //完好数量
            result.qualifiedQuantity = getString(etQualifiedQuantity);
            //损坏数量
            result.damagedQuantity = getString(etDamage);
            //送检数量
            result.inspectionQuantity = getString(etInspectQuantity);
            //锈蚀数量
            result.rustQuantity = getString(etCorrosion);
            //变质
            result.badQuantity = getString(etBad);
            //其他数量
            result.otherQuantity = getString(etOtherQuantity);
            //包装情况
            result.sapPackage = String.valueOf(spPackageCondition.getSelectedItemPosition() + 1);
            //质检单号
            result.qmNum = getString(etQmNum);
            //索赔单号
            result.claimNum = getString(etQueryClaimNum);
            //合格证
            result.certificate = cbCertificate.isChecked() ? "X" : "";
            //说明书
            result.instructions = cbInstructions.isChecked() ? "X" : "";
            //质检证书
            result.qmCertificate = cbQmCertificate.isChecked() ? "X" : "";
            //检验结果
            result.inspectionResult = spInspectionResult.getSelectedItemPosition() == 0 ? "01" : "02";
            result.modifyFlag = "N";
            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, lineData.mapExt);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, lineData.mapExt);

            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadInspectionDataSingle(result));
    }

    @Override
    public void saveCollectedDataSuccess() {
        showMessage("数据保存成功");
        clearCommonUI(etQuantity, etSampleQuantity, etQualifiedQuantity,
                etQmNum, etBad, etOtherQuantity, etDamage, etCorrosion, etInspectQuantity);
        //更新累计数量
        refreshTotalQuantity(etQuantity, tvTotalQuantity);
        refreshTotalQuantity(etSampleQuantity, tvSampleTotalQuantity);
        refreshTotalQuantity(etQualifiedQuantity, tvQualifiedTotalQuantity);
        refreshTotalQuantity(etCorrosion, tvCorrosionTotalQuantity);
        refreshTotalQuantity(etDamage, tvDamageTotalQuantity);
        refreshTotalQuantity(etBad, tvBadTotalQuantity);
        refreshTotalQuantity(etOtherQuantity, tvOtherTotalQuantity);

    }

    private void refreshTotalQuantity(EditText et, TextView tv) {
        float tmp = UiUtil.convertToFloat(getString(et), 0.0f);
        float total = UiUtil.convertToFloat(getString(tv), 0.0f);
        float quantity = ArithUtil.add(tmp, total);
        tv.setText(String.valueOf(quantity));
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage(message);
    }

    /**
     * 拍照之前做必要的检查
     *
     * @return
     */
    protected boolean checkBeforeTakePhoto() {
        if (!etMaterialNum.isEnabled()) {
            showMessage("请先获取单据数据");
            return false;
        }
        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("请先选择单据行");
            return false;
        }
        return true;
    }

    protected void toTakePhoto(String menuName, int takePhotoType) {
        if (!checkBeforeTakePhoto()) {
            return;
        }
        Intent intent = new Intent(mActivity, TakephotoActivity.class);
        Bundle bundle = new Bundle();
        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, "物资验收拍照-" + menuName);
        //拍照类型
        bundle.putInt(Global.EXTRA_TAKE_PHOTO_TYPE, takePhotoType);
        //单据号
        bundle.putString(Global.EXTRA_REF_NUM_KEY, mRefData.recordNum);

        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, mBizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, mRefType);
        //单据行号
        final int selectedLineNum = getIndexByLineNum(mSelectedRefLineNum);
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        //行号
        bundle.putString(Global.EXTRA_REF_LINE_NUM_KEY, lineData.lineNum);
        //行id
        bundle.putString(Global.EXTRA_REF_LINE_ID_KEY, lineData.refLineId);
        bundle.putInt(Global.EXTRA_POSITION_KEY, selectedLineNum);
        bundle.putBoolean(Global.EXTRA_IS_LOCAL_KEY, false);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

    @Override
    public void _onPause() {
        clearAllUI();
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_SAVE_COLLECTION_DATA_ACTION:
                saveCollectedData();
                break;
        }
        super.retry(retryAction);
    }

    /**
     * 清除所有的UI字段
     */
    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvOrderQuantity, tvActQuantity, etQuantity,
                etManufacturer, etCorrosion, etDamage, etBad,
                etQmNum, etInspectQuantity, etQualifiedQuantity, etOtherQuantity,
                etQuantity, etQueryClaimNum, etSampleQuantity, cbCertificate,
                cbInstructions, cbCertificate, cbQmCertificate);

        //库存地点
        if (spInv.getAdapter() != null) {
            spInv.setSelection(0);
        }

        //单据
        if (mRefLineAdapter != null) {
            mRefLines.clear();
            mRefLineAdapter.notifyDataSetChanged();
            spRefLine.setBackgroundColor(0);
        }
        //清除额外资源
        clearExtraUI(mSubFunEntity.collectionConfigs);
    }


}
