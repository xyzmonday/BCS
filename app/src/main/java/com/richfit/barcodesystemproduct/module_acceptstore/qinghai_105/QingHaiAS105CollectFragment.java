package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105;


import android.text.TextUtils;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105.imp.QingHaiAS105CollectPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * 注意该业务比较特别，每一个单据的所有的行明细都是由一行生成拆分出来的，这就意味着
 * 单据中所有的行的refLineId和lineNum都一致，所以不能将它作为唯一的表示来标识该行。
 * 这里采用的方式使用每一行明细的insLot字段来标识每一行的明细。
 * Created by monday on 2017/3/1.
 */

public class QingHaiAS105CollectFragment extends BaseASCollectFragment<QingHaiAS105CollectPresenterImp> {


    EditText etReturnQuantity;
    EditText etProjectText;
    EditText etMoveCauseDesc;
    Spinner spStrategyCode;
    Spinner spMoveReason;

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
        //如果输入的退货交货数量，那么移动原因必输，如果退货交货数量没有输入那么移动原因可输可不输
        etProjectText = (EditText) mActivity.findViewById(R.id.et_project_text);
        etMoveCauseDesc = (EditText) mActivity.findViewById(R.id.et_move_cause_desc);
        spStrategyCode = (Spinner) mActivity.findViewById(R.id.sp_strategy_code);
        spMoveReason = (Spinner) mActivity.findViewById(R.id.sp_move_cause);

        etBatchFlag.setEnabled(false);
        super.initView();
    }

    @Override
    public void initData() {
        if (spStrategyCode != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp,
                    getStringArray(R.array.strategy_codes));
            spStrategyCode.setAdapter(adapter);
        }
        if (spMoveReason != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp,
                    getStringArray(R.array.move_reasons));
            spMoveReason.setAdapter(adapter);
        }
        super.initData();
    }

    /**
     * 通过物料编码和批次匹配单据明细的行。这里我们返回的所有行的insLot集合
     *
     * @param materialNum
     * @param batchFlag
     * @return
     */
    protected Flowable<ArrayList<String>> matchMaterialInfo(final String materialNum, final String batchFlag) {
        if (mRefData == null || mRefData.billDetailList == null ||
                mRefData.billDetailList.size() == 0 || TextUtils.isEmpty(materialNum)) {
            return Flowable.error(new Throwable("请先单据明细"));
        }
        ArrayList<String> insLosts = new ArrayList<>();
        List<RefDetailEntity> list = mRefData.billDetailList;
        for (RefDetailEntity entity : list) {
            if (mIsOpenBatchManager) {
                final String insLot = entity.insLot;
                //如果打开了批次，那么在看明细中是否有批次
                if (!TextUtils.isEmpty(entity.batchFlag) && !TextUtils.isEmpty(batchFlag)) {
                    if (materialNum.equalsIgnoreCase(entity.materialNum) &&
                            batchFlag.equalsIgnoreCase(entity.batchFlag) &&
                            !TextUtils.isEmpty(insLot))

                        insLosts.add(insLot);
                } else {
                    if (materialNum.equalsIgnoreCase(entity.materialNum) &&
                            !TextUtils.isEmpty(insLot))
                        insLosts.add(insLot);
                }
            } else {
                final String lineNum = entity.lineNum;
                //如果明细中没有打开了批次管理,那么只匹配物料编码
                if (materialNum.equalsIgnoreCase(entity.materialNum) && !TextUtils.isEmpty(lineNum))
                    insLosts.add(entity.lineNum);

            }
        }
        if (insLosts.size() == 0) {
            return Flowable.error(new Throwable("未获取到匹配的物料"));
        }
        return Flowable.just(insLosts);
    }


    /**
     * 通过单据行的检验批得到该行在单据明细列表中的位置
     *
     * @param insLot:单据行的检验批
     * @return 返回该行号对应的行明细在明细列表的索引
     */
    protected int getIndexByLineNum(String insLot) {
        int index = -1;
        if (TextUtils.isEmpty(insLot))
            return index;

        if (mRefData == null || mRefData.billDetailList == null
                || mRefData.billDetailList.size() == 0)
            return index;

        for (RefDetailEntity detailEntity : mRefData.billDetailList) {
            index++;
            if (insLot.equalsIgnoreCase(detailEntity.insLot))
                break;

        }
        return index;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> getTransferSingle(getString(etBatchFlag), location));
    }

    @Override
    public void bindCommonCollectUI() {
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        etReturnQuantity.setText(lineData.returnQuantity);
        etMoveCauseDesc.setText(lineData.moveCauseDesc);
        etProjectText.setText(lineData.projectText);
        super.bindCommonCollectUI();
    }

    /**
     * 检查数量是否合理。需要满足
     * 1. 退货交货数量 <= 不不合格量
     * <p>
     * 2. 退货交货数量 + 实收数量 = 检验批数量
     * <p>
     * 3. 实收数量 <= 合格数量
     *
     * @param quantity
     * @return
     */
    protected boolean refreshQuantity(final String quantity) {
        //1.实收数量必须大于0
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);
        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("输入实收数量不合理");
            return false;
        }

        //2.实收数量必须小于合格数量(应收数量)
        final float actQuantityV = UiUtil.convertToFloat(getString(tvActQuantity), 0.0f);
        if (Float.compare(quantityV, actQuantityV) > 0.0f) {
            showMessage("实收数量不能大于应收数量");
            return false;
        }

        // 3. 退货交货数量 <= 不不合格量
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final float unqualifiedQuantityV = UiUtil.convertToFloat(lineData.unqualifiedQuantity, 0.0f);
        final float returnQuantityV = UiUtil.convertToFloat(getString(etReturnQuantity), 0.0f);
        if (Float.compare(returnQuantityV, unqualifiedQuantityV) > 0.0f) {
            showMessage("退货交货数量不能大于不合格数量");
            return false;
        }

        // 4. 退货交货数量 + 实收数量 = 检验批数量
        final float inSlotQuantityV = UiUtil.convertToFloat(lineData.insLotQuantity, 0.0f);
        if (Float.compare(quantityV + returnQuantityV, inSlotQuantityV) != 0.0f) {
            showMessage("实收数量加退货数量不等于检验批数量");
            if (!cbSingle.isChecked())
                etQuantity.setText("");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        //如果退货数量不为o那么移动原因说明必须输入
        if ((!isEmpty(getString(etReturnQuantity)) || !"0".equals(getString(etReturnQuantity)))
                && isEmpty(getString(etMoveCauseDesc))) {
            showMessage("请输入移动原因说明");
            return false;
        }
        return super.checkCollectedDataBeforeSave();
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
            //物料凭证
            result.refDoc = lineData.refDoc;
            //物料凭证单据行
            result.refDocItem = lineData.refDocItem;
            //退货交货数量
            result.returnQuantity = getString(etReturnQuantity);
            //检验批数量
            result.insLot = lineData.insLot;
            //移动原因描述
            result.moveCauseDesc = getString(etMoveCauseDesc);
            //项目文本
            result.projectText = getString(etProjectText);
            //决策代码
            if (spStrategyCode.getSelectedItemPosition() > 0) {
                Object object = spStrategyCode.getSelectedItem();
                if (object != null) {
                    result.decisionCode = object.toString().split("_")[0];
                }
            }
            //移动原因
            if (spMoveReason.getSelectedItemPosition() > 0) {
                Object selectedItem = spMoveReason.getSelectedItem();
                if (selectedItem != null)
                    result.moveCause = selectedItem.toString().split("_")[0];
            }

            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            result.mapExLocation = createExtraMap(Global.EXTRA_LOCATION_MAP_TYPE, lineData.mapExt, mExtraLocationMap);
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCollectionDataSingle(result));
    }


    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
