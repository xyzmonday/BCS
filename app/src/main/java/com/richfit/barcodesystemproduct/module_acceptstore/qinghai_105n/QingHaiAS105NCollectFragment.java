package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n;

import android.text.TextUtils;

import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_105n.imp.QingHaiAS105NCollectPresenterImp;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * 青海105物资入库数据采集界面。对于必检的物资不能使用105非必检入库
 * Created by monday on 2017/2/20.
 */

public class QingHaiAS105NCollectFragment extends BaseASCollectFragment<QingHaiAS105NCollectPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> {
            getTransferSingle(getString(etBatchFlag), location);
        });
    }

    /**
     * 绑定UI。注意重写的目的是判断必检物资不能做105非必检入库
     */
    @Override
    public void bindCommonCollectUI() {
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        //如果是质检，那么不允许进行下面的操作
        if(TextUtils.isEmpty(lineData.qmFlag) && "x".equalsIgnoreCase(lineData.qmFlag)) {
            showMessage("该物料是必检物资不能做105非必检入库");
            return;
        }
        super.bindCommonCollectUI();
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        final String location = getString(etLocation);
        if (TextUtils.isEmpty(location)) {
            showMessage("请输入上架仓位");
            return false;
        }

        if(location.length() > 10) {
            showMessage("您输入的上架不合理");
            return false;
        }

        return super.checkCollectedDataBeforeSave();
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
