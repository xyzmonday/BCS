package com.richfit.barcodesystemproduct.module_check.qinghai_blind.collect;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_check.qinghai_blind.collect.imp.BlindCollectPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by monday on 2017/3/3.
 */

public class QingHaiBlindCollectFragment extends BaseFragment<BlindCollectPresenterImp, Object>
        implements IBlindCollectView {


    @BindView(R.id.ll_check_location)
    LinearLayout llCheckLocation;
    @BindView(R.id.et_check_location)
    EditText etCheckLocation;
    @BindView(R.id.et_material_num)
    RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_material_group)
    TextView tvMaterialGroup;
    @BindView(R.id.et_quantity)
    EditText etQuantity;
    @BindView(R.id.cb_single)
    CheckBox cbSingle;

    @Override
    public void handleBarCodeScanResult(String type, String[] list) {
        if (list != null && list.length > 2) {
            final String materialNum = list[1];
            if (cbSingle.isChecked() && materialNum.equalsIgnoreCase(getString(etMaterialNum))) {
                saveCollectedData();
            } else {
                etMaterialNum.setText(materialNum);
                getCheckTransferInfoSingle(materialNum, getString(etCheckLocation));
            }

        } else if (list != null && list.length == 2 & !cbSingle.isChecked()) {
            final String location = list[1];
            etCheckLocation.setText("");
            etCheckLocation.setText(location);
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_blind_collect;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    protected void initView() {
        //读取额外字段配置信息
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.COLLECT_CONFIG_TYPE, Global.LOCATION_CONFIG_TYPE);
        //如果是库存级，那么盘点仓位不显示
        llCheckLocation.setVisibility(View.GONE);
    }

    /**
     * 注册所有UI事件
     */
    @Override
    public void initEvent() {
       /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            hideKeyboard(etMaterialNum);
            getCheckTransferInfoSingle(materialNum, getString(etCheckLocation));
        });

       /*单品(注意单品仅仅控制实收数量，累计数量是由行信息里面控制)*/
        cbSingle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etQuantity.setText(isChecked ? "1" : "");
            etQuantity.setEnabled(!isChecked);
        });
    }

    /**
     * 读取数据采集界面的配置信息成功，动态生成额外控件
     *
     * @param configs:返回configType=3,4的两种配置文件。
     */
    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.collectionConfigs = configs.get(0);
        mSubFunEntity.locationConfigs = configs.get(1);
        createExtraUI(mSubFunEntity.collectionConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
        createExtraUI(mSubFunEntity.locationConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.collectionConfigs = null;
        mSubFunEntity.locationConfigs = null;
    }

    /**
     * 检查抬头界面的必要的字段是否已经赋值
     */
    @Override
    public void initDataLazily() {
        etMaterialNum.setEnabled(false);
        if (mRefData == null) {
            showMessage("请现在抬头页面初始化本次盘点");
            return;
        }

        if (isEmpty(mRefData.bizType)) {
            showMessage("未获取到业务类型");
            return;
        }

        if (isEmpty(mRefData.checkId)) {
            showMessage("请先在抬头界面初始化本次盘点");
            return;
        }

        if ("01".equals(mRefData.checkLevel) && TextUtils.isEmpty(mRefData.storageNum)) {
            showMessage("请先在抬头界面选择仓库号");
            return;
        }
        if ("02".equals(mRefData.checkLevel) && TextUtils.isEmpty(mRefData.workId)) {
            showMessage("请先在抬头界面选择工厂");
            return;
        }

        if ("02".equals(mRefData.checkLevel) && TextUtils.isEmpty(mRefData.invId)) {
            showMessage("请先在抬头界面选择库存");
            return;
        }

        if (isEmpty(mRefData.voucherDate)) {
            showMessage("请先在抬头界面选择过账日期");
            return;
        }
        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
            return;
        }
        String transferKey = (String) SPrefUtil.getData(mBizType, "0");
        if ("1".equals(transferKey)) {
            showMessage("本次采集已经过账,请先到数据明细界面进行数据上传操作");
            return;
        }
        etMaterialNum.setEnabled(true);
    }

    @Override
    public void getCheckTransferInfoSingle(String materialNum, String location) {
        if (!etMaterialNum.isEnabled()) {
            return;
        }
        if (TextUtils.isEmpty(materialNum)) {
            showMessage("请输入物资条码");
            return;
        }

        if ("01".equals(mRefData.checkLevel) && TextUtils.isEmpty(location)) {
            showMessage("请先输入盘点仓位");
            return;
        }

        clearAllUI();
        mPresenter.getCheckTransferInfoSingle(mRefData.checkId, location, "01", materialNum, mBizType);
    }

    /**
     * 获取盘点库存成功。保存到内存。
     */
    @Override
    public void loadMaterialInfoSuccess(MaterialEntity data) {
        tvMaterialDesc.setText(data.materialDesc);
        tvMaterialGroup.setText(data.materialGroup);
        etMaterialNum.setTag(data.id);
    }

    @Override
    public void loadMaterialInfoFail(String message) {
        showMessage(message);
    }

    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("提示");
        builder.setMessage("您真的确定要提交本次盘点结果吗?");
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
            saveCollectedData();
        });
        builder.show();
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        final float quantityV = UiUtil.convertToFloat(getString(etQuantity), 0.0F);

        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("您输入的盘点数量不合理");
            return false;
        }
        if (mRefData == null) {
            showMessage("请现在抬头页面初始化本次盘点");
            return false;
        }

        if (isEmpty(mRefData.checkId)) {
            showMessage("请先在抬头界面初始化本次盘点");
            return false;
        }

        if ("01".equals(mRefData.checkLevel) && isEmpty(getString(etCheckLocation))) {
            showMessage("请输入盘点仓位");
            return false;
        }

        //检查额外字段是否合格
        if (!checkExtraData(mSubFunEntity.collectionConfigs)) {
            showMessage("请检查输入数据");
            return false;
        }

        if (!checkExtraData(mSubFunEntity.locationConfigs)) {
            showMessage("请检查输入数据");
            return false;
        }

        return true;
    }

    @Override
    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }
        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            ResultEntity result = new ResultEntity();
            result.businessType = mRefData.bizType;
            result.checkId = mRefData.checkId;
            result.workId = mRefData.workId;
            result.invId = mRefData.invId;
            result.location = getString(etCheckLocation);
            result.voucherDate = mRefData.voucherDate;
            result.userId = Global.USER_ID;
            result.workId = mRefData.workId;
            result.invId = mRefData.invId;
            result.materialId = CommonUtil.Obj2String(etMaterialNum.getTag());
            result.quantity = getString(etQuantity);
            result.modifyFlag = "N";
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadCheckDataSingle(result));
    }


    @Override
    public void saveCollectedDataSuccess() {
        showMessage("盘点成功");
        if (!cbSingle.isChecked()) {
            etQuantity.setText("");
        }
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage(message);
    }

    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvMaterialGroup, etQuantity);
        clearExtraUI(mSubFunEntity.collectionConfigs);
        clearExtraUI(mSubFunEntity.locationConfigs);

    }

    @Override
    public void _onPause() {
        clearAllUI();
    }

    @Override
    public void retry(String action) {
        switch (action) {
            case Global.RETRY_LOAD_INVENTORY_ACTION:
                getCheckTransferInfoSingle(getString(etMaterialNum), getString(etCheckLocation));
                break;
        }
        super.retry(action);
    }
}
