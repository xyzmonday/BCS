package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww;

import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * 青海委外入库抬头界面
 * Created by monday on 2017/1/19.
 */

public class QingHaiASWWHeaderFragment extends BaseASHeaderFragment{

    @NonNull
    @Override
    protected String getBizType() {
        return mBizType;
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "1";
    }

    @Override
    public void initInjector() {

    }

}
