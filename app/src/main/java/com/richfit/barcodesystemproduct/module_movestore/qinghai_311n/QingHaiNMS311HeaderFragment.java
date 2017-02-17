package com.richfit.barcodesystemproduct.module_movestore.qinghai_311n;

import android.view.View;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.InvAdapter;
import com.richfit.barcodesystemproduct.module_movestore.baseheader_n.BaseNMSHeaderFragment;
import com.richfit.common_lib.utils.DateChooseHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InvEntity;

import java.util.List;

/**
 * 青海311无参考移库抬头界面
 * Created by monday on 2017/2/16.
 */

public class QingHaiNMS311HeaderFragment extends BaseNMSHeaderFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView() {
        super.initView();
        llRecWork.setVisibility(View.GONE);
        tvSendWorkName.setText("工厂");
    }

    @Override
    public void initEvent() {
        /*选择日期*/
        etTransferDate.setOnRichEditTouchListener((view, text) ->
                DateChooseHelper.chooseDateForEditText(mActivity, etTransferDate, Global.GLOBAL_DATE_PATTERN_TYPE1));
        //发出工厂
        RxAdapterView.itemSelections(spSendWork)
                .filter(position -> position.intValue() > 0)
                .subscribe(position -> mPresenter.getSendInvsByWorkId(mSendWorks.get(position.intValue()).workId, getOrgFlag()));
        mPresenter.deleteCollectionData("", mBizType, Global.USER_ID, mCompanyCode);
    }

    /**
     * 发出库位列表
     *
     * @param sendInvs
     */
    @Override
    public void showSendInvs(List<InvEntity> sendInvs) {
        mSendInvs.clear();
        mSendInvs.addAll(sendInvs);
        if (mSendInvAdapter == null) {
            mSendInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mSendInvs);
            spSendInv.setAdapter(mSendInvAdapter);
        } else {
            mSendInvAdapter.notifyDataSetChanged();
        }
        //同时初始化接收库位
        mRecInvs.clear();
        mRecInvs.addAll(sendInvs);
        if (mRecInvAdapter == null) {
            mRecInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mRecInvs);
            spRecInv.setAdapter(mRecInvAdapter);
        } else {
            mRecInvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void _onPause() {
        super._onPause();
        if(mRefData != null) {
            mRefData.recWorkName = mRefData.workName;
            mRefData.recWorkCode = mRefData.workCode;
            mRefData.recWorkId = mRefData.workId;
        }
    }

    @Override
    public void loadSendInvsFail(String message) {
        showMessage(message);
    }


    @Override
    public void showRecInvs(List<InvEntity> recInvs) {

    }

    @Override
    public void loadRecInvsFail(String message) {
    }

    @Override
    protected String getMoveType() {
        return "5";
    }

    @Override
    protected int getOrgFlag() {
        return 0;
    }

}
