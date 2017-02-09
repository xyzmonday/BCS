package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.collect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.camera.TakephotoActivity;
import com.richfit.common_lib.utils.Global;

/**
 * 庆阳验收清单验收的数据采集模块，用户扫描物料后，仅仅提供拍照功能。
 * Created by monday on 2017/1/16.
 */

public class QingYangAO_1CollectFragment extends QingYangAOCollectFragment {


    @Override
    public void initDataLazily() {
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
        setEnanble(etMaterialNum,tvActQuantity,spInv,spRefLine);
    }

    private void setEnanble(View ...views) {
        for (View view : views) {
            view.setEnabled(false);
        }
    }

    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
        GridView menu = (GridView) rootView.findViewById(R.id.gridview);
        BottomMenuAdapter adapter;
        mMenuNames.clear();
        //1：质量证明文件，2：技术附件，3：外观照片，4：其他
        mMenuNames.add("质量证明文件");
        mMenuNames.add("技术附件");
        mMenuNames.add("外观照片");
        mMenuNames.add("其他");
        //确定拍照类型
        for (int i = 0, size = mMenuNames.size(); i < size; i++) {
            mTakePhotoTypes.add(i+1);
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
            toTakePhoto(mMenuNames.get(position % mMenuNames.size()), mTakePhotoTypes.get(position));
            dialog.dismiss();
        });
    }

    @Override
    protected void toTakePhoto(String menuName, int takePhotoType) {
        Intent intent = new Intent(mActivity, TakephotoActivity.class);
        Bundle bundle = new Bundle();
        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, "物资验收拍照-" + menuName);
        //拍照类型
        bundle.putInt(Global.EXTRA_TAKE_PHOTO_TYPE, takePhotoType);
        //单据号
        bundle.putString(Global.EXTRA_REF_NUM_KEY, mRefData.recordNum);
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, mBizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, mRefType);
        //在线离线模式
        bundle.putBoolean(Global.EXTRA_IS_LOCAL_KEY, false);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

}
