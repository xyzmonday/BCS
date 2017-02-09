package com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy;

import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.module_delivery.baseheader.BaseDSHeaderFragment;
import com.richfit.domain.bean.RefNumEntity;

import java.util.List;

/**
 * Created by monday on 2017/1/17.
 */

public class QingYangDSYHeaderFragment extends BaseDSHeaderFragment {

    private static final String MOVE_TYPE = "1";

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

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void loadRefNumListSuccess(List<RefNumEntity> list) {

    }

    @Override
    public void loadRefNumListFail(String message) {

    }
}
