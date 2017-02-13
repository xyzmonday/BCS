package com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.adapter.QingHaiUbSto351DetailAdapter;
import com.richfit.barcodesystemproduct.module_movestore.basedetail.BaseMSDetailFragment;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * Created by monday on 2017/2/10.
 */

public class QingHaiUbSto351DetailFragment extends BaseMSDetailFragment {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void showNodes(List<RefDetailEntity> allNodes) {
        for (RefDetailEntity node : allNodes) {
            if (!TextUtils.isEmpty(node.transId)) {
                mTransId = node.transId;
                break;
            }
        }
        QingHaiUbSto351DetailAdapter adapter = new QingHaiUbSto351DetailAdapter(mActivity, allNodes,
                mSubFunEntity.parentNodeConfigs, mSubFunEntity.childNodeConfigs,
                mCompanyCode);
        mRecycleView.setAdapter(adapter);
        adapter.setOnItemEditAndDeleteListener(this);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && QingHaiUbSto351DetailAdapter.class.isInstance(adapter)) {
            QingHaiUbSto351DetailAdapter detailAdapter = (QingHaiUbSto351DetailAdapter) adapter;
            detailAdapter.removeNodeByPosition(position);
        }
    }

    @Override
    public void submitBarcodeSystemSuccess() {

    }

    @Override
    public void submitSAPSuccess() {

    }


    @Override
    protected String getSubFunName() {
        return "351移库";
    }
}
