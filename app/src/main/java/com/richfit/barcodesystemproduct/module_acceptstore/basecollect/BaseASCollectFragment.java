package com.richfit.barcodesystemproduct.module_acceptstore.basecollect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
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
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import rx.android.schedulers.AndroidSchedulers;


/**
 * 物资入库有参考基类.
 * 配置文件说明:
 * 1. 服务器返回的额外字段分别保存在了抬头的mapExt,行的mapExt，以及仓位的mapExt
 * 2. configType = 0,数据在抬头的mapExt,config=1,3在行的mapExt；
 * config=2,4在仓位的mapExt。
 * 对于最复杂的101物资入库，对于非质检物资一定上架，对于质检物资那么允许不上架
 * Created by monday on 2016/11/15.
 */

public abstract class BaseASCollectFragment<P extends IASCollectPresenter> extends BaseFragment<P, Object>
        implements IASCollectView {

    @BindView(R.id.ref_line_num_spinner)
    protected Spinner spRefLine;
    @BindView(R.id.et_material_num)
    protected RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_special_inv_flag)
    TextView tvSpecialInvFlag;
    @BindView(R.id.tv_work)
    TextView tvWork;
    @BindView(R.id.tv_work_name)
    protected TextView tvWorkName;
    @BindView(R.id.act_quantity_name)
    protected TextView tvActQuantityName;
    @BindView(R.id.tv_act_quantity)
    protected TextView tvActQuantity;
    @BindView(R.id.et_batch_flag)
    protected EditText etBatchFlag;
    @BindView(R.id.inv_spinner)
    protected Spinner spInv;
    @BindView(R.id.et_location)
    protected RichEditText etLocation;
    @BindView(R.id.tv_location_quantity)
    protected TextView tvLocQuantity;
    @BindView(R.id.quantity_name)
    protected TextView tvQuantityName;
    @BindView(R.id.et_quantity)
    protected EditText etQuantity;
    @BindView(R.id.cb_single)
    protected CheckBox cbSingle;
    @BindView(R.id.tv_total_quantity)
    protected TextView tvTotalQuantity;
    @BindView(R.id.ll_return_quantity)
    protected LinearLayout llReturnQuantity;
    @BindView(R.id.et_return_quantity)
    protected EditText etReturnQuantity;

    /*是否不上架.对于非质检的物资isNLocation=false。也就是说子类如果不处理那么默认需要输入上架仓位*/
    protected boolean isNLocation;
    /*当前匹配的行明细（行号）*/
    protected ArrayList<String> mRefLines;
    /*单据行适配器*/
    ArrayAdapter<String> mRefLineAdapter;
    /*库存地点列表*/
    protected ArrayList<InvEntity> mInvDatas;
    /*库存地点适配器*/
    private InvAdapter mInvAdapter;
    /*累计数量*/
//    protected float mCurrentTotalQuantity;
    protected String mSelectedRefLineNum;
    /*缓存的批次*/
    String mCachedBatchFlag;
    /*缓存的仓位级别的额外字段*/
    Map<String, Object> mExtraLocationMap;
    //校验仓位是否存在，如果false表示校验该仓位不存在或者没有校验该仓位，不允许保存数据
    protected boolean isLocationChecked = false;

    @Override
    protected int getContentId() {
        return R.layout.fragment_base_as_collect;
    }

    /**
     * 在没有勾选单品的情况下：
     * 1）不论扫当前和是其他的物料统统先清除控件的信息;
     * 如果勾选了单品，那么：
     * 1）如果扫的是当前的物料，那么仓位数量和累计数量+1,直接保存;
     * 2）如果扫的不是当前的物料，那么清空所有的控件，并且重新走没有单品的情况下的逻辑；
     * 3）如果扫描的是仓位，那么不处理。
     *
     * @param type
     * @param list
     */
    @Override
    public void handleBarCodeScanResult(String type, String[] list) {
        if (list != null && list.length >= 12) {
            final String materialNum = list[2];
            final String batchFlag = list[11];
            if (cbSingle.isChecked() && materialNum.equalsIgnoreCase(getString(etMaterialNum))) {
                //如果已经选中单品，那么说明已经扫描过一次。必须保证每一次的物料都一样
                getTransferSingle(batchFlag, getString(etLocation));
            } else {
                //在单品模式下，扫描不同的物料
                loadMaterialInfo(materialNum, batchFlag);
            }
            //处理仓位
        } else if (list != null && list.length == 1 & !cbSingle.isChecked()) {
            final String location = list[0];
            etLocation.setText(location);
            getTransferSingle(getString(etBatchFlag), location);
        }
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        mRefLines = new ArrayList<>();
        mInvDatas = new ArrayList<>();
    }

    @Override
    protected void initView() {
        //读取额外字段配置信息
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.COLLECT_CONFIG_TYPE, Global.LOCATION_CONFIG_TYPE);
    }

    /**
     * 绑定公共事件，子类自己根据是否上架，是否需要检查上架是否存在
     * 重写上架仓位监听
     */
    @Override
    public void initEvent() {
        /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            hideKeyboard(etMaterialNum);
            //手动输入没有批次
            loadMaterialInfo(materialNum, getString(etBatchFlag));
        });

       /*单据行*/
        RxAdapterView
                .itemSelections(spRefLine)
                .filter(position -> position > 0)
                .subscribe(position -> bindCommonCollectUI());

        /*单品(注意单品仅仅控制实收数量，累计数量是由行信息里面控制)*/
        cbSingle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etQuantity.setText(isChecked ? "1" : "");
            etQuantity.setEnabled(!isChecked);
        });

        //监听上架仓位时时变化
        RxTextView.textChanges(etLocation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    tvLocQuantity.setText("");
                    tvTotalQuantity.setText("");
                });
    }

    /**
     * 检查抬头界面的必要的字段是否已经赋值
     */
    @Override
    public void initDataLazily() {
        etMaterialNum.setEnabled(false);
        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.bizType)) {
            showMessage("未获取到业务类型");
            return;
        }

        if (TextUtils.isEmpty(mRefData.moveType)) {
            showMessage("未获取到移动类型");
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
        String transferKey = (String) SPrefUtil.getData(mBizType + mRefType, "0");
        if ("1".equals(transferKey)) {
            showMessage("本次采集已经过账,请先到数据明细界面进行数据上传操作");
            return;
        }
        etMaterialNum.setEnabled(true);
        etBatchFlag.setEnabled(mIsOpenBatchManager);
        etLocation.setEnabled(!isNLocation);
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

    /**
     * 输入或者扫描物料条码后系统自动去匹配单据行明细，并且初始化默认选择的明细数据.
     * 由于在初始化单据行下拉列表的同时也需要出发页面刷星，所以页面刷新统一延迟到选择单据
     * 行列表的item之后。
     * 具体流程loadMaterialInfo->setupRefLineAdapter->bindCommonCollectUI
     */
    @Override
    public void loadMaterialInfo(@NonNull String materialNum, @NonNull String batchFlag) {
        if (!etMaterialNum.isEnabled()) {
            return;
        }
        if (TextUtils.isEmpty(materialNum)) {
            showMessage("请输入物资条码");
            return;
        }
        clearAllUI();
        etBatchFlag.setText(batchFlag);
        //刷新界面(在单据行明细查询是否有该物料条码，如果有那么刷新界面)
        matchMaterialInfo(materialNum, batchFlag)
                .compose(TransformerHelper.io2main())
                .subscribe(details -> setupRefLineAdapter(details), e -> showMessage(e.getMessage()));
    }

    /**
     * 设置单据行
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
            showMessage("该单据中未查询到该物料,请检查物资编码或者批次是否正确");
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
     * 绑定UI。
     */
    @Override
    public void bindCommonCollectUI() {
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        etQuantity.setText("");
        //物资描述
        tvMaterialDesc.setText(lineData.materialDesc);
        //特殊库存标识
        tvSpecialInvFlag.setText(lineData.specialInvFlag);
        //工厂
        tvWork.setText(lineData.workName);
        //应收数量
        tvActQuantity.setText(lineData.actQuantity);
        //批次
        if (TextUtils.isEmpty(getString(etBatchFlag))) {
            etBatchFlag.setText(mIsOpenBatchManager ? lineData.batchFlag : "");
        }
        //先将库存地点选择器打开，获取缓存后在判断是否需要锁定
        spInv.setEnabled(true);
        //初始化额外字段的数据,注意这仅仅是服务器返回的数据，不含有任何缓存数据。
        bindExtraUI(mSubFunEntity.collectionConfigs, lineData.mapExt);
        if (!cbSingle.isChecked())
            mPresenter.getInvsByWorkId(lineData.workId, getOrgFlag());
    }

    /**
     * 获取单条缓存。
     * 在获取仓位数量的缓存之前，必须检查仓位是否合理。注意不同的公司检查的策略不一样。
     */
    protected void getTransferSingle(String batchFlag, String location) {

        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请先选择单据行");
            return;
        }
        //检验是否选择了库存地点
        if (spInv.getSelectedItemPosition() == 0) {
            showMessage("请先选择库存地点");
            return;
        }

        //批次处理
        if (mIsOpenBatchManager)
            if (TextUtils.isEmpty(batchFlag)) {
                showMessage("请先输入批次");
                return;
            }
        if (TextUtils.isEmpty(location)) {
            showMessage("请先输入上架仓位");
            return;
        }
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final String invId = mInvDatas.get(spInv.getSelectedItemPosition()).invId;
        mPresenter.checkLocation("04", lineData.workId, invId, batchFlag, location);
    }

    @Override
    public void checkLocationFail(String message) {
        showMessage(message);
        isLocationChecked = false;
    }

    @Override
    public void checkLocationSuccess(String batchFlag, String location) {
        isLocationChecked = true;
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final String refCodeId = mRefData.refCodeId;
        final String refLineId = lineData.refLineId;
        final String refType = mRefData.refType;
        final String bizType = mRefData.bizType;
        mPresenter.getTransferInfoSingle(refCodeId, refType, bizType, refLineId,
                batchFlag, location, Global.USER_ID);
    }

    @Override
    public void loadInvFail(String message) {
        showMessage(message);
    }

    @Override
    public void showInvs(ArrayList<InvEntity> list) {
        //初始化库存地点
        mInvDatas.clear();
        mInvDatas.addAll(list);
        if (mInvAdapter == null) {
            mInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mInvDatas);
            spInv.setAdapter(mInvAdapter);
        } else {
            mInvAdapter.notifyDataSetChanged();
        }
        //默认选择第一个
        spInv.setSelection(0);
    }

    /**
     * 不论扫描的是否是同一个物料，都清除控件的信息。
     */
    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvWork, tvActQuantity, etLocation,
                etBatchFlag, tvLocQuantity, etQuantity, tvLocQuantity,
                tvTotalQuantity, cbSingle);

        //单据行
        if (mRefLineAdapter != null) {
            mRefLines.clear();
            mRefLineAdapter.notifyDataSetChanged();
            spRefLine.setBackgroundColor(0);
        }
        //库存地点
        if (mInvAdapter != null) {
            mInvDatas.clear();
            mInvAdapter.notifyDataSetChanged();
        }
        spInv.setEnabled(true);
        //清除额外资源
        clearExtraUI(mSubFunEntity.collectionConfigs);
        clearExtraUI(mSubFunEntity.locationConfigs);
    }

    /**
     * 绑定缓存。主要显示缓存的仓位数量，以及扩展字段。
     * 注意在bindCommonCollectUI方法中，系统需要判断是否上架，主要的逻辑是，
     * 用户输入物料和批次后，得到该行的物料信息，该物料信息包含了该物料是否是质检
     * 物资，如果是非质检物质那么isNLocation=false,表示必须上架；如果是质检物质
     * 那么需要通过该父节点下的第一个子节点是否录入了仓位。如果第一个子节点由仓位
     * 那么isNLocation = false,否者isNLocation=true。
     *
     * @param cache：缓存数据
     * @param batchFlag：批次
     * @param location：仓位
     */
    @Override
    public void onBindCache(RefDetailEntity cache, String batchFlag, String location) {
        if (!isNLocation) {
            //如果上架，那么必须保证输入批次与单据的批次一致
            RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            if (mIsOpenBatchManager && !TextUtils.isEmpty(lineData.batchFlag)) {
                if (!lineData.batchFlag.equalsIgnoreCase(batchFlag)) {
                    showMessage("您输入的批次有误，请重新输入");
                    return;
                }
            }
            //当前输入批次是否与缓存的批次一致
            if (mIsOpenBatchManager && !TextUtils.isEmpty(mCachedBatchFlag)) {
                if (!mCachedBatchFlag.equalsIgnoreCase(batchFlag)) {
                    showMessage("您输入的批次有误，请重新输入");
                    return;
                }
            }
            if (cache != null) {
                tvTotalQuantity.setText(cache.totalQuantity);
                //查询该行的locationInfo
                List<LocationInfoEntity> locationInfos = cache.locationList;
                if (locationInfos == null || locationInfos.size() == 0) {
                    //没有缓存
                    tvLocQuantity.setText("0");
                    return;
                }
                //如果有缓存，但是可能匹配不上
                tvLocQuantity.setText("0");
                //匹配每一个缓存
                for (LocationInfoEntity info : locationInfos) {
                    mCachedBatchFlag = info.batchFlag;
                    if (location.equalsIgnoreCase(info.location) &&
                            batchFlag.equalsIgnoreCase(info.batchFlag)) {
                        mExtraLocationMap = info.mapExt;
                        tvLocQuantity.setText(info.quantity);
                        break;
                    }
                }

                //锁定库存地点
                final String cachedInvId = cache.invId;
                if (!TextUtils.isEmpty(cachedInvId)) {
                    int pos = -1;
                    for (InvEntity data : mInvDatas) {
                        pos++;
                        if (cachedInvId.equals(data.invId))
                            break;
                    }
                    spInv.setEnabled(false);
                    spInv.setSelection(pos);
                }
            }
        }
    }

    @Override
    public void loadCacheSuccess() {
        showMessage("获取缓存成功");
         /*绑定仓位级别的额外数据*/
        bindExtraUI(mSubFunEntity.locationConfigs, mExtraLocationMap);
        if (cbSingle.isChecked() && checkCollectedDataBeforeSave()) {
            saveCollectedData();
        }
    }

    @Override
    public void loadCacheFail(String message) {
        spInv.setEnabled(true);
        showMessage(message);
        tvLocQuantity.setText("0");
        tvTotalQuantity.setText("0");
        mCachedBatchFlag = "";
        mExtraLocationMap = null;
        if (cbSingle.isChecked() && checkCollectedDataBeforeSave()) {
            saveCollectedData();
        }
    }

    /**
     * 处理输入实收数量和累计数量
     * 父节点记录了前一次累计数量，所以这里仅仅将当前的入库数量与前一次的累计数量相加即可。
     */
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
            if (!cbSingle.isChecked())
                etQuantity.setText("");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        if (!isLocationChecked) {
            showMessage("您输入的仓位不存在");
            return false;
        }
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
        //库存地点
        if (spInv.getSelectedItemPosition() == 0) {
            showMessage("请先选择库存地点");
            return false;
        }

        //物资条码
        if (TextUtils.isEmpty(getString(etMaterialNum))) {
            showMessage("请先输入物料条码");
            return false;
        }

        //批次
        if (mIsOpenBatchManager) {
            if (TextUtils.isEmpty(getString(etBatchFlag))) {
                showMessage("请先输入批次");
                return false;
            }
            // 必须保证所有的批次一致
            final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            //是否与父节点的批次一致
            if (mIsOpenBatchManager && !TextUtils.isEmpty(lineData.batchFlag)) {
                if (!lineData.batchFlag.equalsIgnoreCase(getString(etBatchFlag))) {
                    showMessage("批次有误,请重新输入");
                    return false;
                }
            }
            if (mIsOpenBatchManager && !TextUtils.isEmpty(mCachedBatchFlag)
                    && !mCachedBatchFlag.equalsIgnoreCase(getString(etBatchFlag))) {
                showMessage("批次有误,请重新输入");
                return false;
            }
        }

        //实发数量
        if (!cbSingle.isChecked() && TextUtils.isEmpty(getString(etQuantity))) {
            showMessage("请先输入数量");
            return false;
        }

        if (!refreshQuantity(cbSingle.isChecked() ? "1" : getString(etQuantity))) {
            showMessage("实收数量有误");
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
        Builder builder = new Builder(mActivity);
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
            result.businessType = mRefData.bizType;
            result.refCodeId = mRefData.refCodeId;
            result.refCode = mRefData.recordNum;
            result.refLineNum = lineData.lineNum;
            result.voucherDate = mRefData.voucherDate;
            result.refType = mRefData.refType;
            result.moveType = mRefData.moveType;
            result.userId = Global.USER_ID;
            result.refLineId = lineData.refLineId;
            result.workId = lineData.workId;
            result.invId = mInvDatas.get(spInv.getSelectedItemPosition()).invId;
            result.materialId = lineData.materialId;
            result.location = isNLocation ? "barcode" : getString(etLocation);
            result.batchFlag = getString(etBatchFlag);
            result.quantity = getString(etQuantity);
            result.modifyFlag = "N";

            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLocation = createExtraMap(Global.EXTRA_LOCATION_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCollectionDataSingle(result));
    }

    @Override
    public void saveCollectedDataSuccess() {
        showMessage("保存数据成功");
        final float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0f);
        final float locQuantityV = UiUtil.convertToFloat(getString(tvLocQuantity), 0.0f);
        final float totalQuantity = UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        tvTotalQuantity.setText(String.valueOf(totalQuantity + quantityV));
        tvLocQuantity.setText(String.valueOf(quantityV + locQuantityV));
        if (!cbSingle.isChecked()) {
            etQuantity.setText("");
        }
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage("保存数据失败;" + message);
    }

    @Override
    public void _onPause() {
        clearAllUI();
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            //获取单条缓存失败
            case Global.RETRY_LOAD_SINGLE_CACHE_ACTION:
                getTransferSingle(getString(etBatchFlag), getString(etLocation));
                break;
        }
        super.retry(retryAction);
    }


    protected abstract int getOrgFlag();

}
