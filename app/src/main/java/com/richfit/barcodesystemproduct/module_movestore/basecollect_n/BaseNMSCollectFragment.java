package com.richfit.barcodesystemproduct.module_movestore.basecollect_n;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.InvAdapter;
import com.richfit.barcodesystemproduct.adapter.LocationAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
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
 * 物资移库无参考数据采集界面基类。
 * 主要的流程：用户输入物料和批次->获取缓存->选择发出仓位->匹配出仓位数量。
 * 注意这里获取到的缓存包括了所有的仓位级别的缓存
 * Created by monday on 2016/11/20.
 */

public abstract class BaseNMSCollectFragment<P extends INMSCollectPresenter> extends BaseFragment<P, Object>
        implements INMSCollectView {

    @BindView(R.id.et_material_num)
    protected RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    protected TextView tvMaterialDesc;
    @BindView(R.id.tv_material_group)
    protected TextView tvMaterialGroup;
    @BindView(R.id.tv_material_unit)
    TextView tvMaterialUnit;
    @BindView(R.id.et_send_batch_flag)
    protected EditText etSendBatchFlag;
    @BindView(R.id.tv_send_batch_flag_name)
    protected TextView tvSendBatchFlagName;
    @BindView(R.id.tv_send_inv_name)
    protected TextView tvSendInvName;
    @BindView(R.id.sp_send_inv)
    Spinner spSendInv;
    @BindView(R.id.tv_send_loc_name)
    protected TextView tvSendLocName;
    @BindView(R.id.sp_send_loc)
    protected Spinner spSendLoc;
    @BindView(R.id.tv_inv_quantity)
    TextView tvInvQuantity;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocQuantity;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.cb_single)
    CheckBox cbSingle;
    @BindView(R.id.et_rec_loc)
    protected EditText etRecLoc;
    @BindView(R.id.et_rec_batch_flag)
    protected EditText etRecBatchFlag;
    @BindView(R.id.ll_rec_location)
    protected LinearLayout llRecLocation;
    @BindView(R.id.ll_rec_batch)
    protected LinearLayout llRecBatch;
    @BindView(R.id.sp_special_inv_flag)
    Spinner spSpecialInvFlag;

    /*ERP仓库号是否一致，对于ERP仓库号一致的物料，必须输入接收仓位，否者不能输入。但是前提
    * 是必须先知道是否打开了WM。如果没有打开那么不需要查询*/
    protected boolean isOpenWM = true;
    protected boolean isWareHouseSame;

    /*发出库位*/
    private InvAdapter mSendInvAdapter;
    protected List<InvEntity> mSendInvs;
    /*发出仓位*/
    protected List<InventoryEntity> mInventoryDatas;
    private LocationAdapter mSendLocAdapter;

    /*缓存的历史仓位数量*/
    private List<RefDetailEntity> mHistoryDetailList;
    /*缓存的仓位级别的额外字段*/
    Map<String, Object> mCachedExtraLocationMap;
    /*缓存的行级别的额外字段*/
    Map<String, Object> mCachedExtraLineMap;


    @Override
    protected int getContentId() {
        return R.layout.fragment_base_nms_collect;
    }

    /**
     * 处理扫描
     *
     * @param type
     * @param list
     */
    @Override
    public void handleBarCodeScanResult(String type, String[] list) {
        if (list != null && list.length >= 12) {
            if (!etMaterialNum.isEnabled()) {
                showMessage("请先在抬头界面获取相关数据");
                return;
            }
            final String materialNum = list[Global.MATERIAL_POS];
            final String batchFlag = list[Global.BATCHFALG_POS];
            if (cbSingle.isChecked() && materialNum.equalsIgnoreCase(getString(etMaterialNum))) {
                //如果已经选中单品，那么说明已经扫描过一次。必须保证每一次的物料都一样
                saveCollectedData();
            } else {
                etMaterialNum.setText(materialNum);
                etSendBatchFlag.setText(batchFlag);
                loadMaterialInfo(materialNum, batchFlag);
            }
        } else if (list != null && list.length == 1 & !cbSingle.isChecked()) {
            final String location = list[0];
            if (etRecLoc.isFocused()) {
                etRecLoc.setText(location);
                return;
            }
            //扫描发出仓位
            if (spSendLoc.getAdapter() != null) {
                final int size = mInventoryDatas.size();
                for (int i = 0; i < size; i++) {
                    if (mInventoryDatas.get(i).location.equalsIgnoreCase(location)) {
                        spSendLoc.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        mSendInvs = new ArrayList<>();
        mInventoryDatas = new ArrayList<>();
        isOpenWM = getWMOpenFlag();

    }

    @Override
    public void initEvent() {
        /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            //请求接口获取获取物料
            hideKeyboard(view);
            loadMaterialInfo(materialNum, getString(etSendBatchFlag));
        });

        /*监测批次修改，如果修改了批次那么需要重新刷新库存信息和用户已经输入的信息*/
        /**
         * debounce(400, TimeUnit.MILLISECONDS) 当没有数据传入达到400ms之后,才去发送数据
         * throttleFirst(400, TimeUnit.MILLISECONDS) 在每一个400ms内,如果有数据传入就发送.且每个400ms内只发送一次或零次数据.
         * 但是这是使用debonce，resetCommonUIPartly将延迟执行到发出库位显示默认位置之后，导致抬头界面的默认发出库位选择失效
         */
        RxTextView.textChanges(etSendBatchFlag)
                .filter(str -> !TextUtils.isEmpty(str))
//                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(batch -> resetCommonUIPartly());


        /*用户输入或者修改发出批次，同时默认接收批次与发出批次一致*/
        if (etSendBatchFlag.isEnabled() && etRecBatchFlag.isEnabled() &&
                TextUtils.isEmpty(getString(etRecBatchFlag))) {
            RxTextView.textChanges(etSendBatchFlag)
                    .debounce(100, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(str -> etRecBatchFlag.setText(str));
        }

       /*库存地点。选择库存地点获取库存*/
        RxAdapterView.itemSelections(spSendInv)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(position -> {
                    //1. 加载库存
                    loadInventoryInfo(position);
                    //2. 需要确定发出仓位和接收仓位是不是隶属一个ERP仓库号
                    checkWareHouseNum(position);
                });

        /*选择发货仓位，查询历史仓位数量以及历史接收仓位*/
        RxAdapterView
                .itemSelections(spSendLoc)
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
        etSendBatchFlag.setEnabled(mIsOpenBatchManager);
        etRecBatchFlag.setEnabled(mIsOpenBatchManager);
        if (mRefData == null) {
            showMessage("请现在抬头界面输入必要的数据");
            return;
        }

        if (TextUtils.isEmpty(mRefData.moveType)) {
            showMessage("未获取到移动类型");
            return;
        }

        if (!checkHeaderData()) {
            return;
        }

        if (!checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请先在抬头界面输入必要的信息");
            return;
        }

        String transferKey = (String) SPrefUtil.getData(mBizType, "0");
        if ("1".equals(transferKey)) {
            showMessage("本次采集已经过账,请先到数据明细界面进行数据上传操作");
            return;
        }
        etMaterialNum.setEnabled(true);
        if (spSpecialInvFlag.getAdapter() == null) {
            List<String> list = getStringArray(R.array.special_inv_flags);
            ArrayAdapter adapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp, list);
            spSpecialInvFlag.setAdapter(adapter);
        }
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
        mHistoryDetailList = null;
        mPresenter.getTransferInfoSingle(mRefData.bizType, materialNum,
                Global.USER_ID, mRefData.workId, mRefData.invId, mRefData.recWorkId,
                mRefData.recInvId, batchFlag, "", -1);
    }

    @Override
    public void onBindCommonUI(ReferenceEntity refData, String batchFlag) {
        RefDetailEntity data = refData.billDetailList.get(0);
        //刷新UI
        etMaterialNum.setTag(data.materialId);
        tvMaterialDesc.setText(data.materialDesc);
        tvMaterialGroup.setText(data.materialGroup);
        tvMaterialUnit.setText(data.unit);
        etSendBatchFlag.setText(!TextUtils.isEmpty(data.batchFlag) ? data.batchFlag :
                batchFlag);
        etRecBatchFlag.setText(!TextUtils.isEmpty(data.batchFlag) ? data.batchFlag :
                batchFlag);
        mHistoryDetailList = refData.billDetailList;
    }

    @Override
    public void loadTransferSingleInfoFail(String message) {
        showMessage(message);
    }

    @Override
    public void loadTransferSingleInfoComplete() {
        mPresenter.getSendInvsByWorks(mRefData.workId, getOrgFlag());
    }

    /**
     * 加载发出库位成功
     *
     * @param invs
     */
    @Override
    public void showSendInvs(List<InvEntity> invs) {
        mSendInvs.clear();
        mSendInvs.addAll(invs);
        if (mSendInvAdapter == null) {
            mSendInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mSendInvs);
            spSendInv.setAdapter(mSendInvAdapter);
        } else {
            mSendInvAdapter.notifyDataSetChanged();
        }
        if (!TextUtils.isEmpty(mRefData.invCode)) {
            int position = -1;
            for (InvEntity entity : mSendInvs) {
                position++;
                if (mRefData.invCode.equalsIgnoreCase(entity.invCode)) {
                    break;
                }
            }
            spSendInv.setSelection(position);
        }
    }

    @Override
    public void loadSendInvsFail(String message) {
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
        if (mSendLocAdapter != null) {
            mInventoryDatas.clear();
            mSendLocAdapter.notifyDataSetChanged();
        }
        if (TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先输入工厂");
            spSendInv.setSelection(0);
            return;
        }

        if (etMaterialNum.getTag() == null) {
            showMessage("请先获取物料信息");
            spSendInv.setSelection(0);
            return;
        }

        final InvEntity invEntity = mSendInvs.get(position);
        mCachedExtraLocationMap = null;
        mCachedExtraLocationMap = null;
        mPresenter.getInventoryInfo(getInventoryQueryType(), mRefData.workId, invEntity.invId,
                mRefData.workCode, invEntity.invCode, "", getString(etMaterialNum),
                CommonUtil.Obj2String(etMaterialNum.getTag()), "",
                getString(etSendBatchFlag), "", "", getInvType());
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
        if (mSendLocAdapter == null) {
            mSendLocAdapter = new LocationAdapter(mActivity, R.layout.item_simple_sp, mInventoryDatas);
            spSendLoc.setAdapter(mSendLocAdapter);
        } else {
            mSendLocAdapter.notifyDataSetChanged();
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
        final String batchFlag = getString(etSendBatchFlag);

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
        String recLocation = "";
        String recBatchFlag = getString(etRecBatchFlag);
        for (RefDetailEntity detail : mHistoryDetailList) {
            List<LocationInfoEntity> locationList = detail.locationList;
            if (locationList != null && locationList.size() > 0) {
                for (LocationInfoEntity locationInfo : locationList) {
                    final boolean isMatched = mIsOpenBatchManager ? location.equalsIgnoreCase(locationInfo.location)
                            && batchFlag.equalsIgnoreCase(locationInfo.batchFlag) :
                            location.equalsIgnoreCase(locationInfo.location);
                    if (isMatched) {
                        locQuantity = locationInfo.quantity;
                        recLocation = locationInfo.recLocation;
                        recBatchFlag = locationInfo.recBatchFlag;
                        mCachedExtraLocationMap = locationInfo.mapExt;
                        mCachedExtraLineMap = detail.mapExt;
                        break;
                    }
                }
            }
        }
        //绑定额外字段数据
        bindExtraUI(mSubFunEntity.locationConfigs, mCachedExtraLocationMap);
        bindExtraUI(mSubFunEntity.collectionConfigs, mCachedExtraLineMap);
        tvLocQuantity.setText(locQuantity);
        //注意如果缓存中没有接收批次或者接收仓位，或者已经手动赋值,那么不用缓存更新它们
        if (!TextUtils.isEmpty(recLocation) && !TextUtils.isEmpty(getString(etRecLoc)))
            etRecLoc.setText(recLocation);
        if (!TextUtils.isEmpty(recBatchFlag) && !TextUtils.isEmpty(getString(etRecBatchFlag)))
            etRecBatchFlag.setText(recBatchFlag);
    }

    private void resetSendLocation() {
        spSendLoc.setSelection(0, true);
        tvInvQuantity.setText("");
        tvLocQuantity.setText("");
    }

    /**
     * 如果打开了WM那么需要检查仓库号是否一致。
     * 对于工厂内的转储，没有接收工厂，那么接收工厂Id默认为发出工厂
     */
    protected void checkWareHouseNum(int position) {
        if (position <= 0) {
            return;
        }
        final String workId = mRefData.workId;
        final String recWorkId = mRefData.recWorkId;
        final String invCode = mSendInvs.get(position).invCode;
        final String recInvCode = mRefData.recInvCode;
        if (isOpenWM) {
            //没有打开WM，不需要检查ERP仓库号是否一致
            isWareHouseSame = true;
            return;
        }
        if (TextUtils.isEmpty(workId)) {
            showMessage("发出工厂为空");
            return;
        }
        if (TextUtils.isEmpty(invCode)) {
            showMessage("发出库位为空");
            return;
        }

        if (TextUtils.isEmpty(recWorkId)) {
            showMessage("接收工厂为空");
            return;
        }

        if (TextUtils.isEmpty(recInvCode)) {
            showMessage("接收库位为空");
            return;
        }
        mPresenter.checkWareHouseNum(isOpenWM, workId, invCode, recWorkId, recInvCode, getOrgFlag());
    }

    @Override
    public void checkWareHouseSuccess() {
        isWareHouseSame = true;
        etRecLoc.setText(getString(etRecLoc));
        etRecLoc.setEnabled(true);
    }

    @Override
    public void checkWareHouseFail(String message) {
        showMessage(message);
        etRecLoc.setText("");
        etRecLoc.setEnabled(false);
        isWareHouseSame = false;
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
        if (TextUtils.isEmpty(mRefData.recInvId)) {
            showMessage("请先选择发出库位");
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
            result.invId = mSendInvs.get(spSendInv.getSelectedItemPosition()).invId;
            result.recWorkId = mRefData.recWorkId;
            result.recInvId = mRefData.recInvId;
            result.materialId = etMaterialNum.getTag().toString();
            result.batchFlag = getString(etSendBatchFlag);
            result.location = mInventoryDatas.get(spSendLoc.getSelectedItemPosition()).location;
            result.recLocation = getString(etRecLoc);
            result.recBatchFlag = getString(etRecBatchFlag);
            result.quantity = getString(etQuantity);
            result.invType = getInvType();
            result.modifyFlag = "N";
            //这里将specialInvFlag从N变成了""
            result.specialInvFlag = spSpecialInvFlag.getSelectedItemPosition() == 0 ? "N" : CommonUtil.Obj2String(spSpecialInvFlag.getSelectedItem());
            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, mCachedExtraLineMap, mCachedExtraLocationMap);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, mCachedExtraLineMap, mCachedExtraLocationMap);
            result.mapExLocation = createExtraMap(Global.EXTRA_LOCATION_MAP_TYPE, mCachedExtraLineMap, mCachedExtraLocationMap);
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
        clearCommonUI(tvMaterialDesc, tvMaterialGroup, tvMaterialUnit,
                tvInvQuantity, tvLocQuantity, etQuantity, etRecBatchFlag, etRecLoc);

        //发出库位(注意由于发出库位是一进来就加载的,所以不能清理)
        if (spSendInv.getAdapter() != null) {
            spSendInv.setSelection(0);
        }

        //发出仓位
        if (spSendLoc.getAdapter() != null) {
            mInventoryDatas.clear();
            mSendLocAdapter.notifyDataSetChanged();
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
        if (spSendInv.getAdapter() != null) {
            spSendInv.setSelection(0);
        }
        //下架仓位
        if (mInventoryDatas != null && mInventoryDatas.size() > 0 &&
                spSendLoc.getAdapter() != null) {
            mInventoryDatas.clear();
            mSendLocAdapter.notifyDataSetChanged();
        }
        //库存数量
        tvInvQuantity.setText("");
        //历史仓位数量
        tvLocQuantity.setText("");
        //实收数量
        etQuantity.setText("");
        //接收仓位
        etRecLoc.setText("");
    }

    @Override
    public void _onPause() {
        clearAllUI();
        clearCommonUI(etMaterialNum, etSendBatchFlag);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_LOAD_SINGLE_CACHE_ACTION:
                loadMaterialInfo(getString(etMaterialNum), getString(etSendBatchFlag));
                break;
        }
        super.retry(retryAction);
    }

    /**
     * 子类检查必要的抬头界面的字段
     */
    protected abstract boolean checkHeaderData();

    /**
     * 子类返回库存查询的类型
     */
    protected abstract String getInvType();

    protected abstract String getInventoryQueryType();

    /**
     * 子类实现是否打开WM管理。如果打开了WM，那么需要检查发出库位和接收库位
     * 是否在同一个仓库号。
     *
     * @return
     */
    protected abstract boolean getWMOpenFlag();

    protected abstract int getOrgFlag();
}
