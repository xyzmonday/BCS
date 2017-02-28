package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsy;

import android.support.annotation.NonNull;
import android.view.View;

import com.richfit.barcodesystemproduct.module_acceptstore.baseheader.BaseASHeaderFragment;

/**
 * 青海转储退库抬头界面
 * Created by monday on 2017/2/27.
 */

public class QingHaiRSYHeaderFragment extends BaseASHeaderFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        llSendWork.setVisibility(View.VISIBLE);
        llCreator.setVisibility(View.VISIBLE);
        super.initView();
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return "352";
    }

}
