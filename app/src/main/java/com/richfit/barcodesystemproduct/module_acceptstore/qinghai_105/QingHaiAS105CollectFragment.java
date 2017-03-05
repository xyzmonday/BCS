package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105;


import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105.imp.QingHaiAS105CollectPresenterImp;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.Map;

/**
 * Created by monday on 2017/3/1.
 */

public class QingHaiAS105CollectFragment extends BaseASCollectFragment<QingHaiAS105CollectPresenterImp> {


    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        llReturnQuantity.setVisibility(View.VISIBLE);
        super.initView();
    }

    @Override
    public void readConfigsComplete() {
        //这里我们仅仅初始化一次数据字典，那么这就意味着当切换或者调用clearAllUI方法的时候不应该
        //清除字典的数据
        mPresenter.readExtraDataSourceDictionary(mSubFunEntity.collectionConfigs);
    }

    @Override
    public void readExtraDictionarySuccess(Map<String, Object> extraMap) {
        bindExtraUI(mSubFunEntity.collectionConfigs, extraMap);
    }

    @Override
    public void readExtraDictionaryFail(String message) {
        showMessage(message);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> {
            getTransferSingle(getString(etBatchFlag), location);
        });
    }

    /**
     * 检查数量是否合理。需要满足
     * 1. 退货交货数量 <= 合格数量(实收数量)
     *
     * @param quantity
     * @return
     */
    protected boolean refreshQuantity(final String quantity) {
        //1.实收数量必须大于0
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);
        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("输入数量不合理");
            return false;
        }
        final float returnQuantityV = UiUtil.convertToFloat(getString(etReturnQuantity), 0.0f);
        if (Float.compare(returnQuantityV, quantityV) > 0.0f) {
            showMessage("退货交货数量不能大于合格数量(实收数量)");
            return false;
        }
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
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
    protected int getOrgFlag() {
        return 0;
    }
}
