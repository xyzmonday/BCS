package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.edit;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.LocationAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.edit.imp.QingHaiDSNEditPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by monday on 2017/2/27.
 */

public class QingHaiDSNEditFragment extends BaseFragment<QingHaiDSNEditPresenterImp, Object>
        implements IQingHaiDSNEditView {

    @BindView(R.id.tv_material_num)
    TextView tvMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_material_group)
    TextView tvMaterialGroup;
    @BindView(R.id.tv_batch_flag)
    TextView tvBatchFlag;
    @BindView(R.id.tv_inv)
    TextView tvInv;
    @BindView(R.id.sp_location)
    Spinner spLocation;
    @BindView(R.id.tv_inv_quantity)
    TextView tvInvQuantity;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocQuantity;
    @BindView(R.id.et_quantity)
    EditText etQuantity;

    String mLocationId;
    String mQuantity;
    /*修改前的发出仓位*/
    String mLocation;
    /*修改前的其他子节点的发出仓位列表*/
    ArrayList<String> mLocations;

    /*库存列表*/
    private List<InventoryEntity> mInventoryDatas;
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
        return R.layout.fragment_base_dsn_edit;
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
        //选择下架仓位，刷新库存并且请求缓存
        RxAdapterView
                .itemSelections(spLocation)
                .filter(position -> position > 0 && isValidatedSendLocation())
                .subscribe(position -> {
                    //库存数量
                    tvInvQuantity.setText(mInventoryDatas.get(position).invQuantity);
                    //获取缓存
                    loadLocationQuantity(mInventoryDatas.get(position).location,
                            getString(tvBatchFlag), mInventoryDatas.get(position).invQuantity);
                });
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        //物料编码
        final String materialNum = bundle.getString(Global.EXTRA_MATERIAL_NUM_KEY);
        final String materialId = bundle.getString(Global.EXTRA_MATERIAL_ID_KEY);
        //库存地点
        final String invId = bundle.getString(Global.EXTRA_INV_ID_KEY);
        final String invCode = bundle.getString(Global.EXTRA_INV_CODE_KEY);
        //下架仓位
        mLocation = bundle.getString(Global.EXTRA_LOCATION_KEY);
        //发出批次
        final String batchFlag = bundle.getString(Global.EXTRA_BATCH_FLAG_KEY);
        //接收仓位
        final String recLocation = bundle.getString(Global.EXTRA_REC_LOCATION_KEY);
        //接收批次
        final String recBatchFlag = bundle.getString(Global.EXTRA_REC_BATCH_FLAG_KEY);
        //移库数量
        mQuantity = bundle.getString(Global.EXTRA_QUANTITY_KEY);
        //其他子节点的发出仓位列表
        mLocations = bundle.getStringArrayList(Global.EXTRA_LOCATION_LIST_KEY);
        mLocationId = bundle.getString(Global.EXTRA_LOCATION_ID_KEY);
        //绑定数据
        tvMaterialNum.setText(materialNum);
        tvMaterialNum.setTag(materialId);
        tvInv.setText(invCode);
        tvInv.setTag(invId);
        etQuantity.setText(mQuantity);
        tvBatchFlag.setText(batchFlag);

        //获取缓存信息
        mPresenter.getTransferInfoSingle(mRefData.bizType, materialNum,
                Global.USER_ID, mRefData.workId, mRefData.invId, mRefData.recWorkId,
                mRefData.recInvId, batchFlag);
    }

    @Override
    public void onBindCommonUI(ReferenceEntity refData, String batchFlag) {
        RefDetailEntity data = refData.billDetailList.get(0);
        //刷新UI
        tvMaterialNum.setTag(data.materialId);
        tvMaterialDesc.setText(data.materialDesc);
        tvMaterialGroup.setText(data.materialGroup);
        tvBatchFlag.setText(!TextUtils.isEmpty(data.batchFlag) ? data.batchFlag :
                batchFlag);
        mHistoryDetailList = refData.billDetailList;
    }

    @Override
    public void loadTransferSingleInfoComplete() {
        //获取库存信息
        mPresenter.getInventoryInfo("04", mRefData.workId,
                CommonUtil.Obj2String(tvInv.getTag()), mRefData.workCode, getString(tvInv),
                "", getString(tvMaterialNum), tvMaterialNum.getTag().toString(),
                "", getString(tvBatchFlag),"","", "1");
    }

    @Override
    public void loadTransferSingleInfoFail(String message) {
        showMessage(message);
    }

    @Override
    public void showInventory(List<InventoryEntity> list) {
        mInventoryDatas.clear();
        InventoryEntity temp = new InventoryEntity();
        temp.location = "请选择";
        mInventoryDatas.add(temp);
        mInventoryDatas.addAll(list);
        if (mLocAdapter == null) {
            mLocAdapter = new LocationAdapter(mActivity, R.layout.item_simple_sp, mInventoryDatas);
            spLocation.setAdapter(mLocAdapter);
        } else {
            mLocAdapter.notifyDataSetChanged();
        }

        //自动选定用户修改前的发出仓位
        //默认选择已经下架的仓位
        if (TextUtils.isEmpty(mLocation)) {
            spLocation.setSelection(0);
            return;
        }
        int pos = -1;
        for (InventoryEntity loc : mInventoryDatas) {
            pos++;
            if (mLocation.equalsIgnoreCase(loc.location)) {
                break;
            }
        }
        spLocation.setSelection(pos);
    }

    @Override
    public void loadInventoryFail(String message) {
        showMessage(message);
    }

    /**
     * 用户选择发出仓位，匹配该仓位上的仓位数量
     */
    private void loadLocationQuantity(String location, String batchFlag, String invQuantity) {
        tvInvQuantity.setText(invQuantity);
        if (TextUtils.isEmpty(location)) {
            showMessage("发出仓位为空");
            return;
        }

        if (mHistoryDetailList == null) {
            showMessage("请先获取物料信息");
            return;
        }

        String locQuantity = "0";

        for (RefDetailEntity detail : mHistoryDetailList) {
            List<LocationInfoEntity> locationList = detail.locationList;
            if (locationList != null && locationList.size() > 0) {
                for (LocationInfoEntity locationInfo : locationList) {
                    if (!TextUtils.isEmpty(batchFlag)) {
                        if (location.equalsIgnoreCase(locationInfo.location)
                                && batchFlag.equalsIgnoreCase(locationInfo.batchFlag)) {
                            locQuantity = locationInfo.quantity;
                            mExtraLocationMap = locationInfo.mapExt;
                            mExtraLineMap = detail.mapExt;
                            break;
                        }
                    } else {
                        if (location.equalsIgnoreCase(locationInfo.location)) {
                            locQuantity = locationInfo.quantity;
                            mExtraLocationMap = locationInfo.mapExt;
                            mExtraLineMap = detail.mapExt;
                            break;
                        }
                    }
                }
            }
        }
        //绑定额外字段数据
        bindExtraUI(mSubFunEntity.locationConfigs, mExtraLocationMap);
        bindExtraUI(mSubFunEntity.collectionConfigs, mExtraLineMap);
        tvLocQuantity.setText(locQuantity);
    }

    /**
     * 用户修改的仓位不允许与其他子节点的仓位一致
     *
     * @return
     */
    private boolean isValidatedSendLocation() {
        if (TextUtils.isEmpty(mLocation)) {
            return false;
        }
        if (mLocations == null || mLocations.size() == 0)
            return true;
        for (String location : mLocations) {
            if (mLocation.equalsIgnoreCase(location)) {
                showMessage("您修改的仓位不合理,请重新输入");
                spLocation.setSelection(0);
                return false;
            }
        }
        return true;
    }



    @Override
    public boolean checkCollectedDataBeforeSave() {

        //检查是否合理，可以保存修改后的数据
        if (TextUtils.isEmpty(getString(etQuantity))) {
            showMessage("请输入移库数量");
            return false;
        }

        if (Float.parseFloat(getString(etQuantity)) <= 0.0f) {
            showMessage("输入移库数量有误，请重新输入");
            return false;
        }

        if(spLocation.getSelectedItemPosition() == 0) {
            showMessage("请先选择发出仓位");
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

        //修改后的出库数量
        float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0f);
        //是否满足本次录入数量<=库存数量
        final float inventoryQuantity = UiUtil.convertToFloat(getString(tvInvQuantity), 0.0f);
        if (Float.compare(quantityV, inventoryQuantity) > 0.0f) {
            showMessage("移库数量有误,请重新输入");
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
            result.locationId = mLocationId;
            result.invId = CommonUtil.Obj2String(tvInv.getTag());
            result.recWorkId = mRefData.recWorkId;
            result.recInvId = mRefData.recInvId;
            result.materialId = tvMaterialNum.getTag().toString();
            result.batchFlag = getString(tvBatchFlag);
            result.location = mInventoryDatas.get(spLocation.getSelectedItemPosition()).location;
            result.quantity = getString(etQuantity);
            result.modifyFlag = "Y";
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
        showMessage("修改成功");
        tvLocQuantity.setText(getString(etQuantity));
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
            case Global.RETRY_LOAD_SINGLE_CACHE_ACTION:
                mPresenter.getTransferInfoSingle(mRefData.bizType, getString(tvMaterialNum),
                        Global.USER_ID, mRefData.workId, mRefData.invId, mRefData.recWorkId,
                        mRefData.recInvId, getString(tvBatchFlag));
                break;
            case Global.RETRY_SAVE_COLLECTION_DATA_ACTION:
                saveCollectedData();
                break;
        }
        super.retry(retryAction);
    }


}
