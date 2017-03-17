package com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.detail;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.LQDetailAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.detail.imp.LQDetailPresenterImp;
import com.richfit.domain.bean.InventoryEntity;

import java.util.List;

import butterknife.BindView;

/**
 * 有仓位queryType 04 ；没有仓位 01
 * Created by monday on 2017/3/16.
 */

public class LQDetailFragment extends BaseFragment<LQDetailPresenterImp, InventoryEntity>
        implements ILQDetailView {

    LQDetailAdapter mAdapter;

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    @Override
    protected int getContentId() {
        return R.layout.fragment_lq_detail;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected void initView() {
        super.initView();
        recycleView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        recycleView.setHasFixedSize(true);
    }

    /**
     * 检查抬头界面的必要的字段是否已经赋值
     */
    @Override
    public void initDataLazily() {
        if (mRefData == null) {
            showMessage("请先在抬头界面扫描料签");
            return;
        }

        if (TextUtils.isEmpty(mRefData.workCode)) {
            showMessage("扫描料签中未含有工厂信息");
            return;
        }

        if (TextUtils.isEmpty(mRefData.invCode)) {
            showMessage("扫描料签中未含有库存地点信息");
            return;
        }
        startQueryInventoryInfo();
    }

    private void startQueryInventoryInfo() {
        String queryType = isEmpty(mRefData.location) ? "01" : "04";
        mPresenter.getInventoryInfo(queryType, "", "", mRefData.workCode,
                mRefData.invCode, "", mRefData.materialNum, "", mRefData.location,
                mRefData.batchFlag, "", "", "1");
    }

    @Override
    public void showInventory(List<InventoryEntity> list) {
        if (mAdapter == null) {
            mAdapter = new LQDetailAdapter(mActivity, R.layout.item_lq_detail, list);
            recycleView.setAdapter(mAdapter);
        } else {
            mAdapter.addAll(list);
        }
    }

    @Override
    public void loadInventoryFail(String message) {
        showMessage(message);
    }

}
