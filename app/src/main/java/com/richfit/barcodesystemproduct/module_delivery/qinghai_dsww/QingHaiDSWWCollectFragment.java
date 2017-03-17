package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.BaseDSCollectFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww.imp.QingHaiDSWWCollectPresenterImp;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * Created by monday on 2017/3/5.
 */

public class QingHaiDSWWCollectFragment extends BaseDSCollectFragment<QingHaiDSWWCollectPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    protected boolean refreshQuantity(final String quantity) {
        if (Float.valueOf(quantity) <= 0.0f) {
            showMessage("输入数量不合理");
            return false;
        }

        /*lastFlag 委外出库行数量判断标识如果 lastFlag = 'X'  则累计录入数量不能大于 应发数量*/
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        if (lineData != null) {
            if (!"X".equalsIgnoreCase(lineData.lastFlag)) {
                return true;
            }
        }

        float totalQuantityV = 0.0f;
        //累计数量
        totalQuantityV += UiUtil.convertToFloat(getString(tvTotalQuantity), 0.0f);
        //应该数量
        final float actQuantityV = UiUtil.convertToFloat(getString(tvActQuantity), 0.0f);
        //本次出库数量
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);
        if (Float.compare(quantityV + totalQuantityV, actQuantityV) > 0.0f) {
            showMessage("输入数量有误，请重新输入");
            etQuantity.setText("");
            return false;
        }
        //该仓位的历史出库数量
        final float historyQuantityV = UiUtil.convertToFloat(getString(tvLocQuantity), 0.0f);
        //该仓位的库存数量
        final float inventoryQuantity = UiUtil.convertToFloat(getString(tvInvQuantity), 0.0f);
        if (Float.compare(quantityV + historyQuantityV, inventoryQuantity) > 0.0f) {
            showMessage("输入数量有误，请重新输入");
            etQuantity.setText("");
            return false;
        }
        return true;
    }

    @Override
    protected String getInvType() {
        return "01";
    }

    @Override
    protected String getInventoryQueryType() {
        return getString(R.string.inventoryQueryTypeSAPLocation);
    }


    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
