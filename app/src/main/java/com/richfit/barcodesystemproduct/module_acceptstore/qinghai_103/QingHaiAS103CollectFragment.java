package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.imp.ASCollectPresenterImp;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiAS103CollectFragment extends BaseASCollectFragment<ASCollectPresenterImp>{

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        super.initVariable(savedInstanceState);
        mIsOpenBatchManager = false;
        //因为不上架，那么仓位自动检查
        isLocationChecked = true;
        isNLocation = true;
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {

        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
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
    protected int getOrgFlag() {
        return 0;
    }
}
