package com.richfit.barcodesystemproduct.module_movestore.qingyang_301n;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.QingYangNMS301DetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_movestore.basedetail_n.BaseNMSDetailFragment;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by monday on 2017/2/8.
 */

public class QingYangNMS301DetailFragment extends BaseNMSDetailFragment {


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
        setRefreshing(true, "加载明细成功");
        QingYangNMS301DetailAdapter adapter = new QingYangNMS301DetailAdapter(mActivity,
                R.layout.base_nms_detail_item,allNodes,mConfigs, null,mCompanyCode);
        mRecycleView.setAdapter(adapter);
        adapter.setOnItemEditAndDeleteListener(this);
    }

    @Override
    public void deleteNodeSuccess(int position) {
        showMessage("删除成功");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && QingYangNMS301DetailAdapter.class.isInstance(adapter)) {
            QingYangNMS301DetailAdapter detailAdapter = (QingYangNMS301DetailAdapter) adapter;
            detailAdapter.removeItemByPosition(position);
            int itemCount = detailAdapter.getItemCount();
            if (itemCount == 0) {
                mExtraContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        SPrefUtil.saveData(mBizType, "0");
        RecyclerView.Adapter adapter = mRecycleView.getAdapter();
        if (adapter != null && QingYangNMS301DetailAdapter.class.isInstance(adapter)) {
            QingYangNMS301DetailAdapter detailAdapter = (QingYangNMS301DetailAdapter) adapter;
            detailAdapter.removeAllVisibleNodes();
        }
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }


    @Override
    protected String getSubFunName() {
        return "301无参考移库";
    }

    @Override
    protected List<String> getBottomMenuTitles() {
        return Arrays.asList(MENUS_NAMES).subList(0,1);
    }
}
