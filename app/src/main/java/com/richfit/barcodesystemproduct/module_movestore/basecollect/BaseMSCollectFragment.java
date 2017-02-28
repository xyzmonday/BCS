package com.richfit.barcodesystemproduct.module_movestore.basecollect;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.richfit.barcodesystemproduct.module_movestore.basecollect.imp.MSCollectPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
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
 * Created by monday on 2017/2/10.
 */

public abstract class BaseMSCollectFragment extends BaseFragment<MSCollectPresenterImp, Object>
        implements IMSCollectView {

    @BindView(R.id.ref_line_num_spinner)
    protected Spinner spRefLine;
    @BindView(R.id.et_material_num)
    protected RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.send_work_name)
    protected TextView sendWorkName;
    @BindView(R.id.tv_send_work)
    TextView tvSendWork;
    @BindView(R.id.tv_act_move_quantity)
    TextView tvActQuantity;
    @BindView(R.id.et_send_batch_flag)
    EditText etSendBatchFlag;
    @BindView(R.id.sp_send_inv)
    Spinner spSendInv;
    @BindView(R.id.sp_send_loc)
    Spinner spSendLoc;
    @BindView(R.id.tv_inv_quantity)
    TextView tvInvQuantity;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocQuantity;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.cb_single)
    CheckBox cbSingle;
    @BindView(R.id.tv_total_quantity)
    TextView tvTotalQuantity;
    @BindView(R.id.et_rec_loc)
    protected EditText etRecLoc;
    @BindView(R.id.et_rec_batch_flag)
    protected EditText etRecBatchFlag;
    @BindView(R.id.ll_rec_location)
    protected LinearLayout llRecLocation;
    @BindView(R.id.ll_rec_batch)
    protected LinearLayout llRecBatch;

    /*单据行选项*/
    private List<String> mRefLines;
    ArrayAdapter<String> mRefLineAdapter;
    /*库存地点*/
    private List<InvEntity> mInvDatas;
    private InvAdapter mInvAdapter;
    /*库存信息*/
    private List<InventoryEntity> mInventoryDatas;
    private LocationAdapter mLocationAdapter;
    /*当前操作的明细行号*/
    private String mSelectedRefLineNum;
    /*缓存的批次*/
    private String mCachedBatchFlag;
    /*缓存的仓位级别的额外字段*/
    private Map<String, Object> mExtraLocationMap;

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
            final String materialNum = list[2];
            final String batchFlag = list[11];
            if (cbSingle.isChecked() && materialNum.equalsIgnoreCase(getString(etMaterialNum))) {
                //如果已经选中单品，那么说明已经扫描过一次。必须保证每一次的物料都一样
                getTransferSingle(spSendLoc.getSelectedItemPosition());
            } else if (!cbSingle.isChecked()) {
                clearAllUI();
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
    protected int getContentId() {
        return R.layout.fragment_base_ms_collect;
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        mRefLines = new ArrayList<>();
        mInvDatas = new ArrayList<>();
        mInventoryDatas = new ArrayList<>();
    }

    @Override
    protected void initView() {
        //读取额外字段配置信息
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.COLLECT_CONFIG_TYPE, Global.LOCATION_CONFIG_TYPE);
    }

    /**
     * 注册所有UI事件
     */
    @Override
    public void initEvent() {
       /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            hideKeyboard(etMaterialNum);
            loadMaterialInfo(materialNum, getString(etSendBatchFlag));
        });

        /*监测批次修改，如果修改了批次那么需要重新刷新库存信息和用户已经输入的信息.
         这里需要注意的是，如果库存地点没有初始化完毕，修改批次不刷新UI。*/
        RxTextView.textChanges(etSendBatchFlag)
                .filter(str -> !TextUtils.isEmpty(str))
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(batch -> resetCommonUIPartly());

        /*监听单据行*/
        RxAdapterView.itemSelections(spRefLine)
                .filter(position -> position > 0)
                .subscribe(position -> bindCommonCollectUI());

        /*库存地点，选择库存地点加载库存数据*/
        RxAdapterView.itemSelections(spSendInv)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                //注意工厂和库存地点必须使用行里面的
                .subscribe(position -> loadInventory(position.intValue()));

       /*下架仓位,选择下架仓位刷新库存数量，并且获取缓存*/
        RxAdapterView
                .itemSelections(spSendLoc)
                .filter(position -> (mInventoryDatas != null && mInventoryDatas.size() > 0 &&
                        position.intValue() < mInventoryDatas.size()))
                .subscribe(position -> getTransferSingle(position));

       /*单品(注意单品仅仅控制实收数量，累计数量是由行信息里面控制)*/
        cbSingle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etQuantity.setText(isChecked ? "1" : "");
            etQuantity.setEnabled(!isChecked);
        });
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
     * 检查抬头界面的必要的字段是否已经赋值
     */
    @Override
    public void initDataLazily() {
        etMaterialNum.setEnabled(false);
        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (isEmpty(mRefData.bizType)) {
            showMessage("未获取到业务类型");
            return;
        }
        if (isEmpty(mRefData.moveType)) {
            showMessage("未获取到移动类型");
            return;
        }

        if (isEmpty(mRefData.refType)) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (isEmpty(mRefData.voucherDate)) {
            showMessage("请先在抬头界面选择过账日期");
            return;
        }
        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
            return;
        }
        String state = (String) SPrefUtil.getData(mBizType + mRefType, "0");
        if (!"0".equals(state)) {
            showMessage("本次采集已经过账,请先到数据明细界面进行数据上传操作");
            return;
        }
        etMaterialNum.setEnabled(true);
        //控制批次
        etSendBatchFlag.setEnabled(mIsOpenBatchManager);
    }

    @Override
    public void loadMaterialInfo(String materialNum, String batchFlag) {
        if (!etMaterialNum.isEnabled()) {
            return;
        }
        if (TextUtils.isEmpty(materialNum)) {
            showMessage("请输入物资条码");
            return;
        }
        clearAllUI();
        //注意在clearAllUI清除了发出批次，但是明细中没有发出批次信息
        etSendBatchFlag.setText(batchFlag);
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
        //发出工厂
        tvSendWork.setText(lineData.workName);
        //应收数量
        tvActQuantity.setText(lineData.actQuantity);

        //发出批次
        if (TextUtils.isEmpty(getString(etSendBatchFlag))) {
            etSendBatchFlag.setText(mIsOpenBatchManager ? lineData.batchFlag : "");
        }
        etSendBatchFlag.setEnabled(mIsOpenBatchManager);
        //先将库存地点选择器打开，获取缓存后在判断是否需要锁定
        spSendInv.setEnabled(true);
        //初始化额外字段的数据,注意这仅仅是服务器返回的数据，不含有任何缓存数据。
        bindExtraUI(mSubFunEntity.collectionConfigs, lineData.mapExt);
        if (!cbSingle.isChecked())
            mPresenter.getInvsByWorkId(lineData.workId, getOrgFlag());
    }

    /**
     * 加载库存
     *
     * @param position:用户选择的库存地点
     */
    private void loadInventory(int position) {
        tvInvQuantity.setText("");
        tvLocQuantity.setText("");
        tvTotalQuantity.setText("");
        if (mLocationAdapter != null) {
            mInventoryDatas.clear();
            mLocationAdapter.notifyDataSetChanged();
        }
        if (position <= 0) {
            return;
        }
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final InvEntity invEntity = mInvDatas.get(position);
        mPresenter.getInventoryInfo(getInventoryQueryType(), lineData.workId, invEntity.invId,
                lineData.workCode, invEntity.invCode, "", getString(etMaterialNum),
                lineData.materialId, "", getString(etSendBatchFlag),lineData.specialInvFlag,mRefData.supplierNum, getInvType());
    }

    @Override
    public void loadInvFail(String message) {
        showMessage(message);
        spSendInv.setSelection(0);
    }

    @Override
    public void showInvs(ArrayList<InvEntity> list) {
        //初始化库存地点
        mInvDatas.clear();
        mInvDatas.addAll(list);
        if (mInvAdapter == null) {
            mInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mInvDatas);
            spSendInv.setAdapter(mInvAdapter);
        } else {
            mInvAdapter.notifyDataSetChanged();
        }
        //默认选择第一个
        spSendInv.setSelection(0);
    }

    /**
     * 加载库存成功
     *
     * @param list
     */
    @Override
    public void showInventory(List<InventoryEntity> list) {
        mInventoryDatas.clear();
        InventoryEntity tmp = new InventoryEntity();
        tmp.location = "请选择";
        mInventoryDatas.add(tmp);
        mInventoryDatas.addAll(list);
        if (mLocationAdapter == null) {
            mLocationAdapter = new LocationAdapter(mActivity, R.layout.item_simple_sp, mInventoryDatas);
            spSendLoc.setAdapter(mLocationAdapter);
        } else {
            mLocationAdapter.notifyDataSetChanged();
        }
        spSendLoc.setSelection(0);
    }

    @Override
    public void loadInventoryFail(String message) {
        showMessage(message);
    }

    /**
     * 获取单条缓存
     */
    protected void getTransferSingle(int position) {
        final String invQuantity = mInventoryDatas.get(position).invQuantity;
        final String location = mInventoryDatas.get(position).location;
        final String batchFlag = getString(etSendBatchFlag);

        if (position <= 0) {
            resetSendLocation();
            return;
        }

        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请先选择单据行");
            resetSendLocation();
            return;
        }
        //检验是否选择了库存地点
        if (spSendInv.getSelectedItemPosition() == 0) {
            showMessage("请先选择库存地点");
            resetSendLocation();
            return;
        }

        if (mIsOpenBatchManager)
            if (TextUtils.isEmpty(batchFlag)) {
                showMessage("请先输入批次");
                resetSendLocation();
                return;
            }
        if (TextUtils.isEmpty(location)) {
            showMessage("请先输入发出仓位");
            resetSendLocation();
            return;
        }

        tvInvQuantity.setText(invQuantity);

        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final String refCodeId = mRefData.refCodeId;
        final String refType = mRefData.refType;
        final String bizType = mRefData.bizType;
        final String refLineId = lineData.refLineId;

        mPresenter.getTransferInfoSingle(refCodeId, refType, bizType, refLineId,
                batchFlag, location, Global.USER_ID);
    }

    private void resetSendLocation() {
        spSendLoc.setSelection(0, true);
        tvInvQuantity.setText("");
        tvLocQuantity.setText("");
        tvTotalQuantity.setText("");
    }

    /**
     * 加载该仓位的仓位数量（注意如果可以请求仓位历史数据时，那么说明不需要考虑在物料是否是质检。）
     * 是否上架的标志位必须在refreshUI方法中应确定好,这里必须检查批次是否一致。
     *
     * @param batchFlag
     * @param location
     */
    @Override
    public void onBindCache(RefDetailEntity cache, String batchFlag, String location) {
        if (cache != null) {
            tvTotalQuantity.setText(cache.totalQuantity);
            //查询该行的locationInfo
            List<LocationInfoEntity> locationInfos = cache.locationList;
            if (locationInfos == null || locationInfos.size() == 0) {
                //没有缓存
                tvLocQuantity.setText("0");
                return;
            }

            //当前输入批次是否与缓存的批次一致
            if (mIsOpenBatchManager && !TextUtils.isEmpty(mCachedBatchFlag)) {
                if (!mCachedBatchFlag.equalsIgnoreCase(batchFlag)) {
                    showMessage("您输入的批次有误，请重新输入");
                    return;
                }
            }

            //如果有缓存，但是可能匹配不上
            tvLocQuantity.setText("0");
            //匹配每一个缓存
            for (LocationInfoEntity info : locationInfos) {
                if (mIsOpenBatchManager) {
                    if (location.equalsIgnoreCase(info.location) &&
                            batchFlag.equalsIgnoreCase(info.batchFlag)) {
                        mExtraLocationMap = info.mapExt;
                        mCachedBatchFlag = info.batchFlag;
                        tvLocQuantity.setText(info.quantity);
                        break;
                    }
                } else {
                    if (location.equalsIgnoreCase(info.location)) {
                        mExtraLocationMap = info.mapExt;
                        mCachedBatchFlag = info.batchFlag;
                        tvLocQuantity.setText(info.quantity);
                        break;
                    }
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
                spSendInv.setEnabled(false);
                spSendInv.setSelection(pos);
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
        spSendInv.setEnabled(true);
        showMessage(message);
        //如果没有获取到任何缓存
        tvLocQuantity.setText("0");
        tvTotalQuantity.setText("0");
        mCachedBatchFlag = "";
        mExtraLocationMap = null;
        if (cbSingle.isChecked() && checkCollectedDataBeforeSave()) {
            saveCollectedData();
        }
    }

    /**
     * 不论扫描的是否是同一个物料，都清除控件的信息。
     */
    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvSendWork, tvActQuantity, etSendBatchFlag, tvLocQuantity,
                etQuantity, tvLocQuantity, tvInvQuantity, tvTotalQuantity, cbSingle);
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
        //下架仓位
        if (mLocationAdapter != null) {
            mInventoryDatas.clear();
            mLocationAdapter.notifyDataSetChanged();
        }

        //清除额外资源
        clearExtraUI(mSubFunEntity.collectionConfigs);
        clearExtraUI(mSubFunEntity.locationConfigs);
    }

    /**
     * 用户修改批次后重置部分UI
     */
    private void resetCommonUIPartly() {
        //如果没有打开批次，那么用户不能输入批次，这里再次拦截
        if (!mIsOpenBatchManager)
            return;
        //库存地点
        if (spSendInv.getAdapter() != null) {
            spSendInv.setEnabled(true);
            spSendInv.setSelection(0);
        }
        //下架仓位
        if (mInventoryDatas != null && mInventoryDatas.size() > 0 &&
                spSendLoc.getAdapter() != null) {
            mInventoryDatas.clear();
            mLocationAdapter.notifyDataSetChanged();
        }
        //库存数量
        tvInvQuantity.setText("");
        //历史仓位数量
        tvLocQuantity.setText("");
        //实收数量
        etQuantity.setText("");
        //累计数量
        tvTotalQuantity.setText("");
    }

    /**
     * 检查数量是否合理。第一：实移数量+累计数量<=应移数量
     *
     * @param quantity:本次出库录入数量
     */
    private boolean refreshQuantity(final String quantity) {
        if (Float.valueOf(quantity) < 0.0f) {
            showMessage("移库数量不合理");
            return false;
        }
        float totalQuantityV = 0.0f;
        //累计数量
        totalQuantityV += UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        //应发数量
        final float actQuantityV = UiUtil.convertToFloat(getString(tvActQuantity), 0.0f);
        //本次出库数量
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);
        if (Float.compare(quantityV + totalQuantityV, actQuantityV) > 0.0f) {
            showMessage("输入实收数量有误，请重新输入");
            etQuantity.setText("");
            return false;
        }
        //该仓位的历史出库数量
        final float historyQuantityV = UiUtil.convertToFloat(getString(tvLocQuantity), 0.0f);
        //该仓位的库存数量
        final float inventoryQuantity = UiUtil.convertToFloat(getString(tvInvQuantity), 0.0f);
        if (Float.compare(quantityV + historyQuantityV, inventoryQuantity) > 0.0f) {
            showMessage("输入实收数量有误，请重新输入");
            etQuantity.setText("");
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
    public boolean checkCollectedDataBeforeSave() {
        //检查数据是否可以保存
        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请先选择单据行");
            return false;
        }
        //物资条码
        if (TextUtils.isEmpty(getString(etMaterialNum))) {
            showMessage("请先输入物料条码");
            return false;
        }
        //发出库位
        if (spSendInv.getSelectedItemPosition() == 0) {
            showMessage("请先选择库存地点");
            return false;
        }

        //批次
        if (mIsOpenBatchManager) {
            if (TextUtils.isEmpty(getString(etSendBatchFlag))) {
                showMessage("请先输入发出批次");
                return false;
            }
            final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            //是否与父节点的发出批次一致
            if (!TextUtils.isEmpty(lineData.batchFlag)) {
                if (!lineData.batchFlag.equalsIgnoreCase(getString(etSendBatchFlag))) {
                    showMessage("输入发出批次有误");
                    return false;
                }
            }
        }
        //实发数量
        if (TextUtils.isEmpty(getString(etQuantity))) {
            showMessage("请先输入数量");
            return false;
        }

        if (!refreshQuantity(getString(etQuantity))) {
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
            result.invId = mInvDatas.get(spSendInv.getSelectedItemPosition()).invId;
            result.materialId = lineData.materialId;
            result.location = mInventoryDatas.get(spSendLoc.getSelectedItemPosition()).location;
            result.batchFlag = getString(etSendBatchFlag).toUpperCase();
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
        final float totalQuantityV = UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        tvLocQuantity.setText(String.valueOf(quantityV + locQuantityV));
        tvTotalQuantity.setText(String.valueOf(totalQuantityV + quantityV));
        if (!cbSingle.isChecked()) {
            etQuantity.setText("");
        }
    }


    @Override
    public void saveCollectedDataFail(String message) {
        showMessage("保存数据失败;" + message);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            //获取单条缓存失败
            case Global.RETRY_LOAD_SINGLE_CACHE_ACTION:
                getTransferSingle(spSendLoc.getSelectedItemPosition());
                break;
        }
        super.retry(retryAction);
    }

    @Override
    public void _onPause() {
        clearAllUI();
    }

    /**
     * 子类返回获取库存类型 "0"表示代管库存,"1"表示正常库存
     *
     * @return
     */
    protected abstract String getInvType();

    protected abstract String getInventoryQueryType();

    protected abstract int getOrgFlag();
}
