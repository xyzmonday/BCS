package com.richfit.barcodesystemproduct.module_movestore.baseedit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.LocationAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_movestore.baseedit.imp.MSEditPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by monday on 2017/2/13.
 */

public abstract class BaseMSEditFragment extends BaseFragment<MSEditPresenterImp, Object>
        implements IMSEditView {

    @BindView(R.id.tv_ref_line_num)
    TextView tvRefLineNum;
    @BindView(R.id.tv_material_num)
    TextView tvMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_send_batch_flag_name)
    protected TextView tvSendBatchFlagName;
    @BindView(R.id.tv_batch_flag)
    TextView tvBatchFlag;
    @BindView(R.id.tv_send_inv_name)
    protected TextView tvSendInvName;
    @BindView(R.id.tv_inv)
    TextView tvInv;
    @BindView(R.id.tv_act_quantity)
    TextView tvActQuantity;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.sp_location)
    Spinner spLocation;
    @BindView(R.id.tv_inv_quantity)
    TextView tvInvQuantity;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocQuantity;
    @BindView(R.id.tv_total_quantity)
    TextView tvTotalQuantity;
    @BindView(R.id.ll_rec_location)
    LinearLayout llRecLocation;
    @BindView(R.id.et_rec_location)
    EditText etRecLoc;
    @BindView(R.id.ll_rec_batch_flag)
    LinearLayout llRecBatchFlag;
    @BindView(R.id.tv_rec_batch_flag)
    TextView tvRecBatchFlag;

    protected String mRefLineId;
    protected String mLocationId;
    protected int mPosition;
    //该子节点修改前的出库数量
    private String mQuantity;
    protected List<InventoryEntity> mInventoryDatas;
    private LocationAdapter mLocationAdapter;
    private List<String> mLocations;
    protected String mSelectedLocation;
    Map<String, Object> mExtraLocationMap;
    private float mTotalQuantity;

    @Override
    protected int getContentId() {
        return R.layout.base_ms_edit;
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        mInventoryDatas = new ArrayList<>();
    }

    @Override
    protected void initView() {
         /*生成额外控件*/
        createExtraUI(mSubFunEntity.collectionConfigs, BaseFragment.EXTRA_VERTICAL_ORIENTATION_TYPE);
        createExtraUI(mSubFunEntity.locationConfigs, BaseFragment.EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void initEvent() {
        //选择下架仓位，刷新库存数量并且请求缓存，注意缓存是用来刷新仓位数量和累计数量
        RxAdapterView
                .itemSelections(spLocation)
                .filter(position -> position >= 0 && isValidatedLocation())
                .subscribe(position -> {
                    //库存数量
                    tvInvQuantity.setText(mInventoryDatas.get(position).invQuantity);
                    //获取缓存
                    mPresenter.getTransferInfoSingle(mRefData.refCodeId, mRefData.refType,
                            mRefData.bizType, mRefLineId, getString(tvBatchFlag), mSelectedLocation
                            , Global.USER_ID);
                });
    }

    /**
     * 用户修改的仓位不允许与其他子节点的仓位一致
     *
     * @return
     */
    private boolean isValidatedLocation() {
        if (TextUtils.isEmpty(mSelectedLocation)) {
            return false;
        }
        for (String location : mLocations) {
            if (mSelectedLocation.equalsIgnoreCase(location)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        mExtraLocationMap = (Map<String, Object>) bundle.getSerializable(Global.LOCATION_EXTRA_MAP_KEY);
        mSelectedLocation = bundle.getString(Global.EXTRA_LOCATION_KEY);
        final String totalQuantity = bundle.getString(Global.EXTRA_TOTAL_QUANTITY_KEY);
        final String batchFlag = bundle.getString(Global.EXTRA_BATCH_FLAG_KEY);
        final String invId = bundle.getString(Global.EXTRA_INV_ID_KEY);
        final String invCode = bundle.getString(Global.EXTRA_INV_CODE_KEY);
        mPosition = bundle.getInt(Global.EXTRA_POSITION_KEY);
        mQuantity = bundle.getString(Global.EXTRA_QUANTITY_KEY);
        mLocations = bundle.getStringArrayList(Global.EXTRA_LOCATION_LIST_KEY);
        mRefLineId = bundle.getString(Global.EXTRA_REF_LINE_ID_KEY);
        mLocationId = bundle.getString(Global.EXTRA_LOCATION_ID_KEY);
        if (mRefData != null) {
            /*单据数据中的库存地点不一定有，而且用户可以录入新的库存地点，所以只有子节点的库存地点才是正确的*/
            final RefDetailEntity lineData = mRefData.billDetailList.get(mPosition);
            //拿到上架总数
            tvRefLineNum.setText(lineData.lineNum);
            tvMaterialNum.setText(lineData.materialNum);
            tvMaterialDesc.setText(lineData.materialDesc);
            tvActQuantity.setText(lineData.actQuantity);
            tvBatchFlag.setText(batchFlag);
            tvInv.setText(invCode);
            tvInv.setTag(invId);

            etQuantity.setText(mQuantity);
            tvTotalQuantity.setText(totalQuantity);
           /*绑定额外字段的数据*/
            bindExtraUI(mSubFunEntity.collectionConfigs, lineData.mapExt, false);
            bindExtraUI(mSubFunEntity.locationConfigs, mExtraLocationMap, false);

            //初始化库存地点
            loadInventoryInfo(lineData.workId, invId, lineData.materialId, "", batchFlag);
        }
    }

    /**
     * 获取库存
     *
     * @param workId
     * @param invId
     * @param materialId
     * @param location
     * @param batchFlag
     */
    protected void loadInventoryInfo(String workId, String invId, String materialId, String
            location, String batchFlag) {
        //检查批次，库存地点等字段
        if (TextUtils.isEmpty(workId)) {
            showMessage("工厂为空");
            return;
        }
        if (TextUtils.isEmpty(invId)) {
            showMessage("库存地点");
            return;
        }

        mPresenter.getInventoryInfo(getInventoryQueryType(), workId, invId, "", "", "", getString(tvMaterialNum), materialId, location, batchFlag, getInvType());
    }

    @Override
    public void showInventory(List<InventoryEntity> list) {
        mInventoryDatas.clear();
        mInventoryDatas.addAll(list);
        if (mLocationAdapter == null) {
            mLocationAdapter = new LocationAdapter(mActivity, R.layout.item_simple_sp, mInventoryDatas);
            spLocation.setAdapter(mLocationAdapter);
        } else {
            mLocationAdapter.notifyDataSetChanged();
        }

        //默认选择已经下架的仓位
        if (TextUtils.isEmpty(mSelectedLocation)) {
            spLocation.setSelection(0);
            return;
        }
        int pos = -1;
        for (InventoryEntity loc : mInventoryDatas) {
            pos++;
            if (mSelectedLocation.equalsIgnoreCase(loc.location)) {
                break;
            }
        }
        spLocation.setSelection(pos);
    }

    @Override
    public void loadInventoryFail(String message) {
        showMessage(message);
    }

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
            //如果有缓存，但是可能匹配不上
            tvLocQuantity.setText("0");
            //匹配每一个缓存
            for (LocationInfoEntity info : locationInfos) {
                if (location.equalsIgnoreCase(info.location) &&
                        batchFlag.equalsIgnoreCase(info.batchFlag)) {
                    tvLocQuantity.setText(info.quantity);
                    break;
                }
            }
        }
    }

    @Override
    public void loadCacheFail(String message) {
        showMessage(message);
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        //检查是否合理，可以保存修改后的数据
        if (TextUtils.isEmpty(getString(etQuantity))) {
            showMessage("请输入入库数量");
            return false;
        }

        if (Float.parseFloat(getString(etQuantity)) <= 0.0f) {
            showMessage("输入出库数量不合理,请重新输入");
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

        //是否满足本次录入数量+累计数量-上次已经录入的出库数量<=应出数量
        float actQuantityV = UiUtil.convertToFloat(getString(tvActQuantity), 0.0f);
        float totalQuantityV = UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        float collectedQuantity = UiUtil.convertToFloat(mQuantity, 0.0f);
        //修改后的出库数量
        float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0f);
        float residualQuantity = totalQuantityV - collectedQuantity + quantityV;//减去已经录入的数量
        if (Float.compare(residualQuantity, actQuantityV) > 0.0f) {
            showMessage("输入实收数量有误");
            etQuantity.setText("");
            return false;
        }

        mQuantity = quantityV + "";
        mTotalQuantity = residualQuantity;
        return true;
    }

    @Override
    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }
        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            RefDetailEntity lineData = mRefData.billDetailList.get(mPosition);
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
            result.invId = CommonUtil.Obj2String(tvInv.getTag());
            result.materialId = lineData.materialId;
            result.location = mInventoryDatas.get(spLocation.getSelectedItemPosition()).location;
            result.batchFlag = getString(tvBatchFlag);
            result.quantity = getString(etQuantity);
            //接收仓位和批次
            result.recLocatin = getString(etRecLoc);
            result.recBatchFlag = getString(tvRecBatchFlag);
            result.modifyFlag = "Y";

            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLocation = createExtraMap(Global.EXTRA_LOCATION_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCollectionDataSingle(result));

    }

    @Override
    public void saveCollectedDataSuccess(String message) {
        tvTotalQuantity.setText(String.valueOf(mTotalQuantity));
        tvLocQuantity.setText(getString(etQuantity));
        showMessage(message);
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage(message);
    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(retryAction);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_LOAD_INVENTORY_ACTION:
                final RefDetailEntity lineData = mRefData.billDetailList.get(mPosition);
                loadInventoryInfo(lineData.workId, tvInv.getTag().toString(), lineData.materialId, "", getString(tvBatchFlag));
                break;
        }
        super.retry(retryAction);
    }

    /**
     * 子类返回获取库存类型 "0"表示代管库存,"1"表示正常库存
     *
     * @return
     */
    protected abstract String getInvType();
    protected abstract String getInventoryQueryType();

}
