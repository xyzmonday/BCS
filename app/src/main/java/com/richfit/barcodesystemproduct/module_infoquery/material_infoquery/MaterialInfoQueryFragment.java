package com.richfit.barcodesystemproduct.module_infoquery.material_infoquery;

import android.text.TextUtils;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_infoquery.material_infoquery.imp.IMaterialInfoQeuryPresenterImp;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.MaterialEntity;

import butterknife.BindView;

/**
 * Created by monday on 2017/3/16.
 */

public class MaterialInfoQueryFragment extends BaseFragment<IMaterialInfoQeuryPresenterImp, MaterialEntity>
        implements IMaterialInfoQeuryView {

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


    @Override
    public void handleBarCodeScanResult(String type, String[] list) {
        if (list != null && list.length >= 12) {
            final String materialNum = list[Global.MATERIAL_POS];
            final String batchFlag = list[Global.BATCHFALG_POS];

            etMaterialNum.setText(materialNum);
            tvBatchFlag.setText(batchFlag);
            loadMaterialInfo(materialNum);
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_material_info_query;
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
            tvMaterialGroup.setText(entity.materialGroup);
            tvMaterialUnit.setText(entity.unit);
        }
    }

    @Override
    public void queryFail(String message) {
        showMessage(message);
    }

    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvMaterialGroup, tvMaterialUnit);
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
