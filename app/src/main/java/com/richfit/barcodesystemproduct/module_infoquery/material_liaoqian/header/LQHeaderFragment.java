package com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.header;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.header.imp.LQHeaderPresenterImp;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.ReferenceEntity;

import butterknife.BindView;

/**
 * 料签查询抬头界面
 * Created by monday on 2017/3/16.
 */

public class LQHeaderFragment extends BaseFragment<LQHeaderPresenterImp, Object>
        implements ILQHeaderView {

    @BindView(R.id.et_material_num)
    RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_material_group)
    TextView tvMaterialGroup;
    @BindView(R.id.tv_material_unit)
    TextView tvMaterialUnit;
    @BindView(R.id.tv_batch_flag)
    TextView tvBatchFlag;


    String workCode;
    String invCode;
    String location;
    String materialGroup;
    String materialNum;
    String materialUnit;
    String batchFlag;

    /**
     * 料签扫描响应。料签的规制是:
     * 1. 工厂
     * 2. 库存地点
     * 3. 仓位
     * 4. 物料组
     * 5. 物料编码
     * 6. 计量单位
     * 7. 批次
     *
     * @param type
     * @param list
     */
    @Override
    public void handleBarCodeScanResult(String type, String[] list) {

        if (list != null && list.length >= 6) {
            workCode = list[0];
            invCode = list[1];
            location = list[2];
            materialGroup = list[3];
            materialNum = list[4];
            materialUnit = list[5];
            batchFlag = list[6];
            loadMaterialInfo(materialNum);
        }
    }

    @Override
    protected void initVariable(@Nullable Bundle savedInstanceState) {
        super.initVariable(savedInstanceState);
        mRefData = null;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_lq_header;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    /**
     * 查询物料信息
     *
     * @param materialNum
     */
    private void loadMaterialInfo(String materialNum) {
        if (TextUtils.isEmpty(materialNum)) {
            showMessage("请输入物料条码");
            return;
        }
        clearAllUI();
        mPresenter.getMaterialInfo("01", materialNum);
    }

    @Override
    public void querySuccess(MaterialEntity entity) {
        if (entity != null) {
            tvMaterialDesc.setText(entity.materialDesc);
            tvMaterialGroup.setText(materialGroup);
            tvBatchFlag.setText(batchFlag);
            tvMaterialUnit.setText(materialUnit);
        }
    }

    @Override
    public void queryFail(String message) {
        showMessage(message);
    }

    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvMaterialGroup, tvMaterialUnit, tvBatchFlag);
    }

    @Override
    public void _onPause() {
        super._onPause();
        if (mRefData == null) {
            mRefData = new ReferenceEntity();
        }
        mRefData.workCode = workCode;
        mRefData.invCode = invCode;
        mRefData.location = location;
        mRefData.materialNum = materialNum;
        mRefData.materialGroup = materialGroup;
        mRefData.materialDesc = getString(tvMaterialDesc);
        mRefData.batchFlag = batchFlag;
    }

    @Override
    public void retry(String action) {
        loadMaterialInfo(getString(etMaterialNum));
        super.retry(action);
    }

    @Override
    public boolean isNeedShowFloatingButton() {
        return false;
    }
}
