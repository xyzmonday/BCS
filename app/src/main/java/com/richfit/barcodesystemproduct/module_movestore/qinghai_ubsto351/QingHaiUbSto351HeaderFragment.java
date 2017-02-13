package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_movestore.baseheader.BaseMSHeaderFragment;

/**
 * 青海UB-Sto351移库抬头界面(采购订单)。
 * 发出工厂没有发出库位
 * Created by monday on 2017/2/10.
 */

public class QingHaiUbSto351HeaderFragment extends BaseMSHeaderFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initView() {
        llInv.setVisibility(View.GONE);
        tvWorkName.setText("发出工厂");
        super.initView();
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "4";
    }


}
