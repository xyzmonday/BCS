package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.collect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.InvAdapter;
import com.richfit.barcodesystemproduct.adapter.LocationAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.collect.imp.QingHaiDSNCollectPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by monday on 2017/2/23.
 */

public class QingHaiDSNCollectFragment extends BaseFragment<QingHaiDSNCollectPresenterImp, Object>
        implements IQingHaiDSNCollectView {


    @BindView(R.id.et_material_num)
    RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_material_group)
    TextView tvMaterialGroup;
    @BindView(R.id.et_batch_flag)
    EditText etBatchFlag;
    @BindView(R.id.sp_inv)
    Spinner spInv;
    @BindView(R.id.sp_location)
    Spinner spLocation;
    @BindView(R.id.tv_inv_quantity)
    TextView tvInvQuantity;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocQuantity;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.cb_single)
    CheckBox cbSingle;


    /*库存地点*/
    private InvAdapter mInvAdapter;
    protected List<InvEntity> mInvs;

    /*下架仓位*/
    protected List<InventoryEntity> mInventoryDatas;
    private LocationAdapter mLocAdapter;

    /*缓存的历史仓位数量*/
    private List<RefDetailEntity> mHistoryDetailList;

    /*缓存的仓位级别的额外字段*/
    Map<String, Object> mExtraLocationMap;
    /*缓存的行级别的额外字段*/
    Map<String, Object> mExtraLineMap;


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_base_dsn_collect;
    }


    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        mInvs = new ArrayList<>();
        mInventoryDatas = new ArrayList<>();
    }

    @Override
    public void initEvent() {
        /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            //请求接口获取获取物料
            hideKeyboard(view);
            loadMaterialInfo(materialNum, getString(etBatchFlag));
        });

        /*监测批次修改，如果修改了批次那么需要重新刷新库存信息和用户已经输入的信息*/
        /**
         * debounce(400, TimeUnit.MILLISECONDS) 当没有数据传入达到400ms之后,才去发送数据
         * throttleFirst(400, TimeUnit.MILLISECONDS) 在每一个400ms内,如果有数据传入就发送.且每个400ms内只发送一次或零次数据.
         * 但是这是使用debonce，resetCommonUIPartly将延迟执行到发出库位显示默认位置之后，导致抬头界面的默认发出库位选择失效
         */
        RxTextView.textChanges(etBatchFlag)
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(str -> !TextUtils.isEmpty(str))
                .subscribe(batch -> resetCommonUIPartly());

       /*库存地点。选择库存地点获取库存*/
        RxAdapterView.itemSelections(spInv)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(position -> loadInventoryInfo(position));

        /*选择发货仓位，查询历史仓位数量以及历史接收仓位*/
        RxAdapterView
                .itemSelections(spLocation)
                .filter(position -> (mInventoryDatas != null && mInventoryDatas.size() > 0 &&
                        position.intValue() < mInventoryDatas.size()))
                .subscribe(position -> loadLocationQuantity(position));

       /*单品(注意单品仅仅控制实收数量，累计数量是由行信息里面控制)*/
        cbSingle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etQuantity.setText(isChecked ? "1" : "");
            etQuantity.setEnabled(!isChecked);
        });
    }

    @Override
    protected void initView() {
        //读取额外字段配置信息
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.COLLECT_CONFIG_TYPE, Global.LOCATION_CONFIG_TYPE);

    }

    @Override
    public void initDataLazily() {
        //检查抬头界面的数据
        etMaterialNum.setEnabled(false);
        etBatchFlag.setEnabled(mIsOpenBatchManager);
        if (mRefData == null) {
            showMessage("请先在抬头界面选择工厂");
            return;
        }
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先在抬头界面选择工厂");
            return;
        }
        if ("26".equals(mBizType) && TextUtils.isEmpty(mRefData.costCenter)) {
            showMessage("请先在抬头界面输入成本中心");
            return;
        }
        if ("27".equals(mBizType) && TextUtils.isEmpty(mRefData.projectNum)) {
            showMessage("请现在抬头界面输入项目编号");
            return;
        }
        etMaterialNum.setEnabled(true);

    }

    /**
     * 读取数据采集界面的配置信息成功，动态生成额外控件
     *
     * @param configs:返回configType=3,4的两种配置文件。
     */
    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.collectionConfigs = configs.get(0);
        mSubFunEntity.locationConfigs = configs.get(1);
        createExtraUI(mSubFunEntity.collectionConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
        createExtraUI(mSubFunEntity.locationConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.collectionConfigs = null;
        mSubFunEntity.locationConfigs = null;
    }

    private void loadMaterialInfo(String materialNum, String batchFlag) {
        if (TextUtils.isEmpty(materialNum)) {
            showMessage("物料编码为空,请重新输入");
            return;
        }
        clearAllUI();
        mPresenter.getTransferInfoSingle(mRefData.bizType, materialNum,
                Global.USER_ID, mRefData.workId, mRefData.invId, mRefData.recWorkId,
                mRefData.recInvId, batchFlag);
    }

    @Override
    public void onBindCommonUI(ReferenceEntity refData, String batchFlag) {
        RefDetailEntity data = refData.billDetailList.get(0);
        //刷新UI
        etMaterialNum.setTag(data.materialId);
        tvMaterialDesc.setText(data.materialDesc);
        tvMaterialGroup.setText(data.materialGroup);
        etBatchFlag.setText(!TextUtils.isEmpty(data.batchFlag) ? data.batchFlag :
                batchFlag);
        mHistoryDetailList = refData.billDetailList;
    }

    @Override
    public void loadTransferSingleInfoFail(String message) {
        showMessage(message);
    }

    @Override
    public void loadTransferSingleInfoComplete() {
        mPresenter.getInvsByWorks(mRefData.workId, 0);
    }

    /**
     * 加载发出库位成功
     *
     * @param invs
     */
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
        if (!TextUtils.isEmpty(mRefData.invCode)) {
            int position = -1;
            for (InvEntity entity : mInvs) {
                position++;
                if (mRefData.invCode.equalsIgnoreCase(entity.invCode)) {
                    break;
                }
            }
            spInv.setSelection(position);
        }
    }

    @Override
    public void loadInvsFail(String message) {
        showMessage(message);
    }

    /**
     * 获取库存信息。注意无参考使用的抬头的工厂信息
     */
    private void loadInventoryInfo(int position) {
        if (position <= 0) {
            return;
        }
        tvInvQuantity.setText("");
        tvLocQuantity.setText("");
        if (mLocAdapter != null) {
            mInventoryDatas.clear();
            mLocAdapter.notifyDataSetChanged();
        }
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先输入工厂");
            spInv.setSelection(0);
            return;
        }

        if (etMaterialNum.getTag() == null) {
            showMessage("请先获取物料信息");
            spInv.setSelection(0);
            return;
        }

        final InvEntity invEntity = mInvs.get(position);
        mPresenter.getInventoryInfo("04", mRefData.workId, invEntity.invId,
                mRefData.workCode, invEntity.invCode, "", getString(etMaterialNum),
                CommonUtil.Obj2String(etMaterialNum.getTag()), "",
                getString(etBatchFlag), "", "", "1");
    }

    /**
     * 加载库存成功
     */
    @Override
    public void showInventory(List<InventoryEntity> list) {
        mInventoryDatas.clear();
        InventoryEntity tmp = new InventoryEntity();
        tmp.location = "请选择";
        mInventoryDatas.add(tmp);
        mInventoryDatas.addAll(list);
        if (mLocAdapter == null) {
            mLocAdapter = new LocationAdapter(mActivity, R.layout.item_simple_sp, mInventoryDatas);
            spLocation.setAdapter(mLocAdapter);
        } else {
            mLocAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadInventoryFail(String message) {
        showMessage(message);
    }

    /**
     * 用户选择发出仓位，匹配该仓位上的仓位数量
     */
    private void loadLocationQuantity(int position) {
        if (position <= 0) {
            resetSendLocation();
            return;
        }
        final String location = mInventoryDatas.get(position).location;
        final String invQuantity = mInventoryDatas.get(position).invQuantity;
        final String batchFlag = getString(etBatchFlag);

        if (mIsOpenBatchManager && TextUtils.isEmpty(batchFlag)) {
            showMessage("请输入发出批次");
            resetSendLocation();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            showMessage("请输入发出仓位");
            resetSendLocation();
            return;
        }

        if (mHistoryDetailList == null) {
            showMessage("请先获取物料信息");
            resetSendLocation();
            return;
        }
        tvInvQuantity.setText(invQuantity);
        String locQuantity = "0";
        for (RefDetailEntity detail : mHistoryDetailList) {
            List<LocationInfoEntity> locationList = detail.locationList;
            if (locationList != null && locationList.size() > 0) {
                for (LocationInfoEntity locationInfo : locationList) {
                    final boolean isMatched = mIsOpenBatchManager ? location.equalsIgnoreCase(locationInfo.location)
                            && batchFlag.equalsIgnoreCase(locationInfo.batchFlag) :
                            location.equalsIgnoreCase(locationInfo.location);
                    if (isMatched) {
                        locQuantity = locationInfo.quantity;
                        mExtraLocationMap = locationInfo.mapExt;
                        mExtraLineMap = detail.mapExt;
                        break;
                    }
                }
            }
        }
        //绑定额外字段数据
        bindExtraUI(mSubFunEntity.locationConfigs, mExtraLocationMap);
        bindExtraUI(mSubFunEntity.collectionConfigs, mExtraLineMap);
        tvLocQuantity.setText(locQuantity);
    }

    private void resetSendLocation() {
        spLocation.setSelection(0, true);
        tvInvQuantity.setText("");
        tvLocQuantity.setText("");
    }

    private boolean refreshQuantity(final String quantity) {
        if (Float.valueOf(quantity) < 0.0f) {
            showMessage("输入数量不合理");
            return false;
        }
        //该仓位的历史出库数量
        //本次出库数量
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);
        final float historyQuantityV = UiUtil.convertToFloat(getString(tvLocQuantity), 0.0f);
        //该仓位的库存数量
        final float inventoryQuantity = UiUtil.convertToFloat(getString(tvInvQuantity), 0.0f);
        if (Float.compare(quantityV + historyQuantityV, inventoryQuantity) > 0.0f) {
            showMessage("输入实收数量有误，请出现输入");
            if (!cbSingle.isChecked())
                etQuantity.setText("");
            return false;
        }
        return true;
    }

    /**
     * 子类自己检查接收仓位和接收批次
     *
     * @return
     */
    @Override
    public boolean checkCollectedDataBeforeSave() {
        if (!etMaterialNum.isEnabled()) {
            showMessage("请先获取物料信息");
            return false;
        }

        if (TextUtils.isEmpty(getString(etMaterialNum))) {
            showMessage("物料编码为空");
            return false;
        }
        if (TextUtils.isEmpty(getString(tvInvQuantity))) {
            showMessage("库存数量为空");
            return false;
        }
        if (TextUtils.isEmpty(getString(tvLocQuantity))) {
            showMessage("仓位数量为空");
            return false;
        }
        //实发数量
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

    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }
        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            ResultEntity result = new ResultEntity();
            result.businessType = mRefData.bizType;
            result.voucherDate = mRefData.voucherDate;
            result.moveType = mRefData.moveType;
            result.userId = Global.USER_ID;
            result.workId = mRefData.workId;
            result.invId = mInvs.get(spInv.getSelectedItemPosition()).invId;
            result.recWorkId = mRefData.recWorkId;
            result.recInvId = mRefData.recInvId;
            result.materialId = etMaterialNum.getTag().toString();
            result.batchFlag = getString(etBatchFlag);
            result.location = mInventoryDatas.get(spLocation.getSelectedItemPosition()).location;
            result.quantity = getString(etQuantity);
            result.costCenter = mRefData.costCenter;
            result.projectNum = mRefData.projectNum;
            result.invType = "1";
            result.modifyFlag = "N";
            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, mExtraLineMap, mExtraLocationMap);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, mExtraLineMap, mExtraLocationMap);
            result.mapExLocation = createExtraMap(Global.EXTRA_LOCATION_MAP_TYPE, mExtraLineMap, mExtraLocationMap);
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCollectionDataSingle(result));
    }

    @Override
    public void saveCollectedDataSuccess() {
        showMessage("保存成功");
        final float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0f);
        final float locQuantityV = UiUtil.convertToFloat(getString(tvLocQuantity), 0.0f);
        tvLocQuantity.setText(String.valueOf(quantityV + locQuantityV));
        if (!cbSingle.isChecked())
            etQuantity.setText("");
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage(message);
    }

    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvMaterialGroup, etBatchFlag,
                tvInvQuantity, tvLocQuantity, etQuantity);

        //库存地点
        if (spInv.getAdapter() != null) {
            spInv.setSelection(0);
        }

        //下架仓位
        if (spLocation.getAdapter() != null) {
            mInventoryDatas.clear();
            mLocAdapter.notifyDataSetChanged();
        }
        //额外字段
        clearExtraUI(mSubFunEntity.collectionConfigs);
        clearExtraUI(mSubFunEntity.locationConfigs);
    }

    /**
     * 用户修改批次后重置部分UI
     */
    protected void resetCommonUIPartly() {
        //如果没有打开批次，那么用户不能输入批次，这里再次拦截
        if (!mIsOpenBatchManager)
            return;
        //库存地点
        if (spInv.getAdapter() != null) {
            spInv.setSelection(0);
        }
        //下架仓位
        if (mInventoryDatas != null && mInventoryDatas.size() > 0 &&
                spLocation.getAdapter() != null) {
            mInventoryDatas.clear();
            mLocAdapter.notifyDataSetChanged();
        }
        //库存数量
        tvInvQuantity.setText("");
        //历史仓位数量
        tvLocQuantity.setText("");
        //实收数量
        etQuantity.setText("");

    }

    @Override
    public void _onPause() {
        super._onPause();
        clearAllUI();
    }

    @Override
    public void networkConnectError(String retryAction) {

    }
}
