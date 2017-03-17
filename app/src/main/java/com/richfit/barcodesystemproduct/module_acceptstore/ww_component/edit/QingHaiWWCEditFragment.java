package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by monday on 2017/3/13.
 */

public class QingHaiWWCEditFragment extends BaseFragment<QingHaiWWCEditPresenterImp, Object>
        implements QingHaiWWCEditContract.IQingHaiWWCEditView {

    @BindView(R.id.tv_material_num)
    TextView tvMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_special_inv_flag)
    TextView tvSpecialInvFlag;
    @BindView(R.id.tv_work)
    TextView tvWork;
    @BindView(R.id.tv_act_quantity)
    TextView tvActQuantity;
    @BindView(R.id.tv_batch_flag)
    TextView tvBatchFlag;
    @BindView(R.id.tv_location_quantity)
    TextView tvLocationQuantity;
    @BindView(R.id.quantity_name)
    TextView quantityName;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.tv_total_quantity)
    TextView tvTotalQuantity;


    int mPosition;
    String mRefLineId;
    String mQuantity;
    String mLocationId;

    @Override
    protected int getContentId() {
        return R.layout.fragment_qinghai_wwc_edit;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        final String totalQuantity = bundle.getString(Global.EXTRA_TOTAL_QUANTITY_KEY);
        final String batchFlag = bundle.getString(Global.EXTRA_BATCH_FLAG_KEY);

        mPosition = bundle.getInt(Global.EXTRA_POSITION_KEY);
        mQuantity = bundle.getString(Global.EXTRA_QUANTITY_KEY);
        mRefLineId = bundle.getString(Global.EXTRA_REF_LINE_ID_KEY);
        mLocationId = bundle.getString(Global.EXTRA_LOCATION_ID_KEY);

        if (mRefData != null && mPosition >= 0) {
            /*单据数据中的库存地点不一定有，而且用户可以录入新的库存地点，所以只有子节点的库存地点才是正确的*/
            final RefDetailEntity lineData = mRefData.billDetailList.get(mPosition);
            //拿到上架总数
            tvMaterialNum.setText(lineData.materialNum);

            tvMaterialDesc.setText(lineData.materialDesc);
            tvBatchFlag.setText(batchFlag);
            tvActQuantity.setText(lineData.actQuantity);
            tvLocationQuantity.setText(mQuantity);
            tvSpecialInvFlag.setText("O");
            etQuantity.setText(mQuantity);
            tvLocationQuantity.setText(mQuantity);
            tvTotalQuantity.setText(totalQuantity);
            tvWork.setText(lineData.workCode);

        }
    }

    /**
     * 保存修改数据前的检查，注意这里之类必须根据业务检查仓位
     *
     * @return
     */
    @Override
    public boolean checkCollectedDataBeforeSave() {

        if (mRefDetail == null) {
            showMessage("未获取到明细数据");
            return false;
        }

        if (TextUtils.isEmpty(getString(etQuantity))) {
            showMessage("请输入入库数量");
            return false;
        }

        float actQuantityV = UiUtil.convertToFloat(getString(tvActQuantity), 0.0f);
        float totalQuantityV = UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        float collectedQuantity = UiUtil.convertToFloat(mQuantity, 0.0f);
        float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0f);
        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("输入数量不合理");
            etQuantity.setText("");
            return false;
        }

        /*lastFlag 委外出库行数量判断标识如果 lastFlag = 'X'  则累计录入数量不能大于 应发数量*/
        RefDetailEntity lineData = mRefDetail.get(mPosition);
        if (lineData != null) {
            if (!"X".equalsIgnoreCase(lineData.lastFlag)) {
                mQuantity = quantityV + "";
                return true;
            }
        }


        float residualQuantity = totalQuantityV - collectedQuantity + quantityV;//减去已经录入的数量
        if (Float.compare(residualQuantity, actQuantityV) > 0.0f) {
            showMessage("输入实收数量有误");
            etQuantity.setText("");
            return false;
        }
        mQuantity = quantityV + "";
        return true;
    }

    @Override
    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }

        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            RefDetailEntity lineData = mRefDetail.get(mPosition);
            ResultEntity result = new ResultEntity();
            result.businessType = mBizType;
            result.refCodeId = mRefData.refCodeId;
            result.refCode = mRefData.recordNum;
            result.refLineNum = lineData.lineNum;
            result.voucherDate = mRefData.voucherDate;
            result.refType = mRefData.refType;
            result.moveType = mRefData.moveType;
            result.userId = Global.USER_ID;
            result.refLineId = lineData.refLineId;
            result.workId = lineData.workId;
            result.materialId = lineData.materialId;
            result.locationId = mLocationId;
            result.location = "barcode";
            result.batchFlag = getString(tvBatchFlag);
            result.quantity = getString(etQuantity);
            result.modifyFlag = "Y";
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
    public void saveEditedDataSuccess(String message) {
        showMessage("修改成功");
        tvLocationQuantity.setText(mQuantity);
        tvTotalQuantity.setText(mQuantity);
    }

    @Override
    public void saveEditedDataFail(String message) {
        showMessage("修改失败");
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

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_EDIT_DATA_ACTION:
                saveCollectedData();
                break;
        }
        super.retry(retryAction);
    }
}
