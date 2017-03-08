package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105;

import android.os.Bundle;
import android.view.ViewStub;
import android.widget.EditText;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_acceptstore.baseedit.BaseASEditFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105.imp.QingHaiAS105EditPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by monday on 2017/3/8.
 */

public class QingHaiAS105EditFragment extends BaseASEditFragment<QingHaiAS105EditPresenterImp> {

    EditText etReturnQuantity;
    EditText etProjectText;
    EditText etMoveCauseDesc;

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        ViewStub stub = (ViewStub) mActivity.findViewById(R.id.viewstub_as_collect);
        stub.inflate();
        //退货交货数量
        etReturnQuantity = (EditText) mActivity.findViewById(R.id.et_return_quantity);
        etReturnQuantity.setEnabled(false);
        //如果输入的退货交货数量，那么移动原因必输，如果退货交货数量没有输入那么移动原因可输可不输
        etProjectText = (EditText) mActivity.findViewById(R.id.et_project_text);
        etMoveCauseDesc = (EditText) mActivity.findViewById(R.id.et_move_cause_desc);
        super.initView();
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        String returnQuantity = bundle.getString(Global.EXTRA_RETURN_QUANTITY_KEY);
        String projectText = bundle.getString(Global.EXTRA_PROJECT_TEXT_KEY);
        String moveCauseDesc = bundle.getString(Global.EXTRA_MOVE_CAUSE_DESC_KEY);
        etReturnQuantity.setText(returnQuantity);
        etProjectText.setText(projectText);
        etMoveCauseDesc.setText(moveCauseDesc);
        //注意实发数量不能修改
        etQuantity.setEnabled(false);
        super.initData();
    }

    /**
     * 读取数据字段成功
     *
     * @param extraMap
     */
    @Override
    public void readExtraDictionarySuccess(Map<String, Object> extraMap) {
        //第一步绑定原始数据
        bindExtraUI(mSubFunEntity.collectionConfigs, extraMap, false);
    }

    @Override
    public void readExtraDictionaryFail(String message) {
        showMessage(message);
    }


    @Override
    public void readExtraDictionaryComplete() {
        //第二步绑定缓存数据
        bindExtraUI(mSubFunEntity.collectionConfigs, mExtraCollectMap, false);
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
            result.voucherDate = mRefData.voucherDate;
            result.refType = mRefData.refType;
            result.moveType = mRefData.moveType;
            result.userId = Global.USER_ID;
            result.refLineId = lineData.refLineId;
            result.workId = lineData.workId;
            result.invId = CommonUtil.Obj2String(tvInv.getTag());
            result.locationId = mLocationId;
            result.materialId = lineData.materialId;
            result.location = getString(etLocation);
            result.batchFlag = getString(tvBatchFlag);
            result.quantity = getString(etQuantity);

            //项目文本
            result.projectText = getString(etProjectText);

            //移动原因说明
            result.moveCauseDesc = getString(etMoveCauseDesc);

            result.modifyFlag = "Y";

            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE,lineData.mapExt, mExtraLocationMap);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLocation = createExtraMap(Global.EXTRA_LOCATION_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCollectionDataSingle(result));
    }
}
