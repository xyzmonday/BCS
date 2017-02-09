package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.collect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.adapter.InvAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.camera.TakephotoActivity;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.collect.imp.ApprovalOtherPresenterImp;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

/**
 * 其他验收。处理逻辑：
 * 扫描物料匹配单据的物料信息->调用getTransferInfoSingle方法获取缓存(获取库存地点和用户数量)
 * Created by monday on 2016/11/23.
 */

public class QingYangAOCollectFragment extends BaseFragment<ApprovalOtherPresenterImp, Object>
        implements IApprovalOtherView {

    public static final String[] INSPECTION_RESULTS = {"请选择", "合格", "不合格"};

    protected static final int[] MENUS_IMAGES = {
            R.mipmap.icon_save_data,
            R.mipmap.icon_take_photo1,
            R.mipmap.icon_take_photo2,
            R.mipmap.icon_take_photo3,
            R.mipmap.icon_take_photo4,
    };

    @BindView(R.id.sp_ref_line_num)
    Spinner spRefLine;
    @BindView(R.id.et_material_num)
    RichEditText etMaterialNum;
    @BindView(R.id.tv_material_desc)
    TextView tvMaterialDesc;
    @BindView(R.id.tv_act_quantity)
    TextView tvActQuantity;
    @BindView(R.id.tv_work)
    TextView tvWork;
    @BindView(R.id.sp_inv)
    protected Spinner spInv;
    @BindView(R.id.et_batch_flag)
    EditText etBatchFlag;
    //合同数量(单据数量)
    @BindView(R.id.tv_order_quantity)
    protected TextView tvOrderQuantity;
    //到货数量
    @BindView(R.id.et_quantity)
    protected EditText etQuantity;
    @BindView(R.id.tv_balance_quantity)
    protected TextView tvBalanceQuantity;//结算数量

    //匹配的检验批行明细
    List<String> mRefLines;

    ArrayAdapter<String> mRefLineAdapter;

    //当前正在操作的单据行号
    String mSelectedRefLineNum;

    ArrayList<String> mMenuNames;

    ArrayList<Integer> mTakePhotoTypes;

    /**
     * 缓存的到货数量
     */
    String mCacheQuantity;
    /**
     * 缓存的库存地点
     */
    String mCacheInvCode;

    /*额外字段的缓存信息*/
    Map<String, Object> mCachedExtraLineMap;

    List<InvEntity> mInvDatas;//库存地点
    InvAdapter mInvAdapter;

    @Override
    protected int getContentId() {
        return R.layout.fragment_qingyang_ao_collect;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    public void handleBarCodeScanResult(String type, String[] list) {

        if (list != null && list.length >= 12) {
            final String materialNum = list[2];
            final String batchFlag = list[11];
            //清除公共UI
            clearCommonUI(etMaterialNum, tvMaterialDesc, tvWork,
                    etBatchFlag, tvOrderQuantity, tvBalanceQuantity, etQuantity);
            //库存地点
            if (spInv.getAdapter() != null) {
                spInv.setSelection(0);
            }
            //单据行
            if (mRefLineAdapter != null) {
                mRefLines.clear();
                mRefLineAdapter.notifyDataSetChanged();
                spRefLine.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }
            loadMaterialInfo(materialNum, batchFlag);
        }
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        mMenuNames = new ArrayList<>();
        mRefLines = new ArrayList<>();
        mTakePhotoTypes = new ArrayList<>();
        mInvDatas = new ArrayList<>();
    }

    @Override
    protected void initView() {
        //读取额外字段配置信息
        mPresenter.readExtraConfigs(mCompanyCode, mBizType, mRefType,
                Global.COLLECT_CONFIG_TYPE);
    }


    @Override
    public void initEvent() {
        /*扫描后者手动输入物资条码*/
        etMaterialNum.setOnRichEditTouchListener((view, materialNum) -> {
            hideKeyboard(etMaterialNum);
            //手动输入没有批次
            loadMaterialInfo(materialNum, getString(etBatchFlag));
        });

        /*单据行*/
        RxAdapterView
                .itemSelections(spRefLine)
                .filter(position -> position > 0)
                .subscribe(position -> getTransferInfoSingle(position.intValue()));
        //监听到货数量
        RxTextView.textChanges(etQuantity)
                .debounce(500, TimeUnit.MILLISECONDS, rx.android.schedulers.AndroidSchedulers.mainThread())
                .filter(str -> !TextUtils.isEmpty(str) && str.length() > 0)
                .subscribe(str -> tvBalanceQuantity.setText(str));
    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {
        mSubFunEntity.collectionConfigs = configs.get(0);
        createExtraUI(mSubFunEntity.collectionConfigs, EXTRA_VERTICAL_ORIENTATION_TYPE);
    }

    @Override
    public void readConfigsFail(String message) {
        showMessage(message);
        mSubFunEntity.collectionConfigs = null;
    }

    @Override
    public void initDataLazily() {
        etMaterialNum.setEnabled(false);
        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.refType)) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.voucherDate)) {
            showMessage("请先在抬头界面选择过账日期");
            return;
        }
        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
            return;
        }
        etMaterialNum.setEnabled(true);
        etBatchFlag.setEnabled(mIsOpenBatchManager);
    }

    /**
     * 加载物料明细信息。
     *
     * @param materialNum:物资条码
     */
    @Override
    public void loadMaterialInfo(final String materialNum, final String batchFlag) {
        if (!etMaterialNum.isEnabled())
            return;

        if (TextUtils.isEmpty(materialNum)) {
            showMessage("请输入物资条码");
            return;
        }
        clearAllUI();
        etBatchFlag.setText(batchFlag);
        //刷新界面(在单据行明细查询是否有该物料条码，如果有那么刷新界面)
        matchMaterialInfo(materialNum,batchFlag)
                .compose(TransformerHelper.io2main())
                .subscribe(details -> setupRefLineAdapter(details), e -> showMessage(e.getMessage()));
    }

    /**
     * 设置单据行适配器
     *
     * @param refLines
     */
    @Override
    public void setupRefLineAdapter(ArrayList<String> refLines) {
        mRefLines.clear();
        mRefLines.add(getString(R.string.default_ref_name));
        if (refLines != null)
            mRefLines.addAll(refLines);

        //如果未查询到提示用户
        if (mRefLines.size() == 1) {
            showMessage("该单据中未查询到该物料,请检查物资编码是否正确");
            spRefLine.setSelection(0);
            return;
        }

        //初始化单据行适配器
        if (mRefLineAdapter == null) {
            mRefLineAdapter = new ArrayAdapter<>(mActivity, R.layout.item_simple_sp, mRefLines);
            spRefLine.setAdapter(mRefLineAdapter);

        } else {
            mRefLineAdapter.notifyDataSetChanged();
        }
        //如果多行设置颜色
        spRefLine.setBackgroundColor(ContextCompat.getColor(mActivity, mRefLines.size() >= 3 ?
                R.color.colorPrimary : R.color.white));

        //默认选择第一个
        spRefLine.setSelection(1);
    }

    /**
     * 获取验收缓存
     *
     * @param index
     */
    private void getTransferInfoSingle(int index) {
        spRefLine.setSelection(index);
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        final String refCodeId = mRefData.refCodeId;
        final String refType = mRefData.refType;
        final String bizType = mRefData.bizType;
        final String refLineId = lineData.refLineId;
        mPresenter.getTransferInfoSingle(refCodeId, refType, bizType, refLineId, getString(etBatchFlag), "",
                Global.USER_ID);
    }

    @Override
    public void onBindCache(RefDetailEntity cache, String batchFlag, String location) {
        if (cache != null) {
            mCacheQuantity = cache.totalQuantity;
            mCacheInvCode = cache.invCode;
            mCachedExtraLineMap = cache.mapExt;
        }
    }

    @Override
    public void loadCacheFail(String message) {
        showMessage(message);
        clearAllUI();
    }

    /**
     * 绑定UI。
     */
    @Override
    public void bindCommonCollectUI() {
        etQuantity.setText("");
        mSelectedRefLineNum = mRefLines.get(spRefLine.getSelectedItemPosition());
        RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        //物资描述
        tvMaterialDesc.setText(lineData.materialDesc);
        //工厂
        tvWork.setText(lineData.workName);
        //单据数量
        tvOrderQuantity.setText(lineData.orderQuantity);
        //应收数量
        tvActQuantity.setText(lineData.actQuantity);
        //到货数量(用户上传输入的数量)
        etQuantity.setText(mCacheQuantity);
        //批次
        if (TextUtils.isEmpty(getString(etBatchFlag))) {
            etBatchFlag.setText(mIsOpenBatchManager ? lineData.batchFlag : "");
        }
        bindExtraUI(mSubFunEntity.collectionConfigs, mCachedExtraLineMap);
        //获取库存地点
        mPresenter.getInvsByWorkId(lineData.workId,0);
    }

    @Override
    public void showInvs(List<InvEntity> invs) {
        //初始化库存地点
        mInvDatas.clear();
        mInvDatas.addAll(invs);
        if (mInvAdapter == null) {
            mInvAdapter = new InvAdapter(mActivity, R.layout.item_simple_sp, mInvDatas);
            spInv.setAdapter(mInvAdapter);
        } else {
            mInvAdapter.notifyDataSetChanged();
        }
        int position = -1;
        if (!TextUtils.isEmpty(mCacheInvCode)) {
            //如果有缓存，那么系统自动选择缓存
            for (InvEntity data : mInvDatas) {
                position++;
                if (mCacheInvCode.equals(data.invCode)) {
                    break;
                }
            }
        }
        //默认选择第一个
        spInv.setSelection(position == -1 ? 0 : position);
    }

    @Override
    public void loadInvsFail(String message) {
        showMessage(message);
        SpinnerAdapter adapter = spInv.getAdapter();
        if (adapter != null && InvAdapter.class.isInstance(adapter)) {
            mInvDatas.clear();
            InvAdapter invAdapter = (InvAdapter) adapter;
            invAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 清除所有的UI字段
     */
    private void clearAllUI() {
        clearCommonUI(tvMaterialDesc, tvWork, etBatchFlag, tvActQuantity, tvOrderQuantity, tvBalanceQuantity, etQuantity);

        //库存地点
        if (spInv.getAdapter() != null) {
            spInv.setSelection(0);
        }

        //单据
        if (mRefLineAdapter != null) {
            mRefLines.clear();
            mRefLineAdapter.notifyDataSetChanged();
            spRefLine.setBackgroundColor(0);
        }
        //清除额外资源
        clearExtraUI(mSubFunEntity.collectionConfigs);
    }

    /**
     * 检查用户输入的到货数量是否合理，必须保证到货数量小于等于应收数量而且大于等于0
     *
     * @param quantity
     * @return
     */
    private boolean refreshQuantity(final String quantity) {

        //检验批数量
        final float actQuantityV = UiUtil.convertToFloat(getString(tvOrderQuantity), 0.0f);
        final float quantityV = UiUtil.convertToFloat(quantity, 0.0f);

        if (Float.compare(quantityV, 0.0f) <= 0.0f) {
            showMessage("输入实收数量有误,请出现输入");
            etQuantity.setText("");
            return false;
        }
        if (Float.compare(quantityV, actQuantityV) > 0.0f) {
            showMessage("输入实收数量有误,请出现输入");
            etQuantity.setText("");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        //检查数据是否可以保存
        if (spRefLine.getSelectedItemPosition() == 0) {
            showMessage("请选选择单据行");
            return false;
        }
        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("请选选择单据行");
            return false;
        }

        if (spInv.getSelectedItemPosition() == 0) {
            showMessage("请选择库存点");
        }

        if (!refreshQuantity(getString(tvActQuantity))) {
            return false;
        }

        //批次信息必须与单据中的一样，这里批次在单据中一定有，但是防止用户修改
        if (mIsOpenBatchManager) {
            RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            final String batchFlag = lineData.batchFlag;
            if (!TextUtils.isEmpty(batchFlag) &&
                    !batchFlag.equalsIgnoreCase(getString(etBatchFlag))) {
                showMessage("批次与单据中的批次不一致");
                return false;
            }
        }

        if (!checkExtraData(mSubFunEntity.collectionConfigs)) {
            showMessage("请先输入必输字段");
            return false;
        }
        return true;
    }

    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
        GridView menu = (GridView) rootView.findViewById(R.id.gridview);
        BottomMenuAdapter adapter = null;
        mMenuNames.clear();
        //1：质量证明文件，2：技术附件，3：外观照片，4：其他
        mMenuNames.add("保存数据");
        mMenuNames.add("质量证明文件");
        mMenuNames.add("技术附件");
        mMenuNames.add("外观照片");
        mMenuNames.add("其他");
        //确定拍照类型
        for (int i = 0, size = mMenuNames.size(); i < size; i++) {
            mTakePhotoTypes.add(i);
        }


        adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu, mMenuNames, MENUS_IMAGES);
        menu.setAdapter(adapter);

        final Dialog dialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
        dialog.setContentView(rootView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        menu.setOnItemClickListener((adapterView, view, position, id) -> {
            switch (position) {
                case 0:
                    //1.保存数据
                    saveCollectedData();
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    //2.拍照
                    toTakePhoto(mMenuNames.get(position % mMenuNames.size()), mTakePhotoTypes.get(position - 1));
                    break;
            }
            dialog.dismiss();
        });
    }

    @Override
    public void saveCollectedData() {
        if (!checkCollectedDataBeforeSave()) {
            return;
        }
        Flowable.create((FlowableOnSubscribe<ResultEntity>) emitter -> {
            RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
            ResultEntity result = new ResultEntity();
            result.refCodeId = mRefData.refCodeId;
            result.refLineId = lineData.refLineId;
            result.businessType = mRefData.bizType;
            result.refType = mRefData.refType;
            result.moveType = mRefData.moveType;
            result.inspectionType = mRefData.inspectionType;
            result.inspectionPerson = Global.USER_ID;
            result.userId = Global.USER_ID;
            result.invId = mInvDatas.get(spInv.getSelectedItemPosition()).invId;
            result.quantity = getString(etQuantity);
            result.modifyFlag = "N";
            result.mapExHead = createExtraMap(Global.EXTRA_HEADER_MAP_TYPE, lineData.mapExt);
            result.mapExLine = createExtraMap(Global.EXTRA_LINE_MAP_TYPE, lineData.mapExt);

            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER).compose(TransformerHelper.io2main())
                .subscribe(result -> mPresenter.uploadInspectionDataSingle(result));
    }

    @Override
    public void saveCollectedDataSuccess() {
        showMessage("数据保存成功");
    }

    @Override
    public void saveCollectedDataFail(String message) {
        showMessage(message);
    }

    /**
     * 拍照之前做必要的检查
     *
     * @return
     */
    protected boolean checkBeforeTakePhoto() {
        if (!etMaterialNum.isEnabled()) {
            showMessage("请先获取单据数据");
            return false;
        }
        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("请先选择单据行");
            return false;
        }
        return true;
    }

    protected void toTakePhoto(String menuName, int takePhotoType) {
        if (!checkBeforeTakePhoto()) {
            return;
        }
        Intent intent = new Intent(mActivity, TakephotoActivity.class);
        Bundle bundle = new Bundle();
        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, "物资验收拍照-" + menuName);
        //拍照类型
        bundle.putInt(Global.EXTRA_TAKE_PHOTO_TYPE, takePhotoType);
        //单据号
        bundle.putString(Global.EXTRA_REF_NUM_KEY, mRefData.recordNum);

        L.e("拍照bizType" + mBizType);
        L.e("拍照refType" + mRefType);
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, mBizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, mRefType);
        //单据行号
        final int selectedLineNum = getIndexByLineNum(mSelectedRefLineNum);
        final RefDetailEntity lineData = getLineData(mSelectedRefLineNum);
        //行号
        bundle.putString(Global.EXTRA_REF_LINE_NUM_KEY, lineData.lineNum);
        //行id
        bundle.putString(Global.EXTRA_REF_LINE_ID_KEY, lineData.refLineId);
        bundle.putInt(Global.EXTRA_POSITION_KEY, selectedLineNum);
        bundle.putBoolean(Global.EXTRA_IS_LOCAL_KEY, false);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(retryAction);
    }

    @Override
    public void _onPause() {
        clearAllUI();
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_SAVE_COLLECTION_DATA_ACTION:
                saveCollectedData();
                break;
        }
        super.retry(retryAction);
    }
}
