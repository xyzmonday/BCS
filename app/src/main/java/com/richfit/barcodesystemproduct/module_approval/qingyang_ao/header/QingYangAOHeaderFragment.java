package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.header;

import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.module_approval.baseheader.BaseApprovalHeaderFragment;


/**
 * 庆阳验收其他抬头界面
 * Created by monday on 2016/11/23.
 */

public class QingYangAOHeaderFragment extends BaseApprovalHeaderFragment {


    private static final String MOVE_TYPE = "00";

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @NonNull
    @Override
    protected String getBizType() {
        return mBizType;
    }

    @NonNull
    @Override
    protected String getMoveType() {
        return MOVE_TYPE;
    }


}
