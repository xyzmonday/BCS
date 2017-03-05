package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2017/2/28.
 */

public class QingHaiAODetailAdapter extends CommonTreeAdapter<RefDetailEntity> {



    public QingHaiAODetailAdapter(Context context, int layoutId,
                                  List<RefDetailEntity> allNodes,
                                  List<RowConfig> parentNodeConfigs,
                                  List<RowConfig> childNodeConfigs,
                                  String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(R.id.refLineNum,item.lineNum)
                .setText(R.id.specialInvFlag,item.specialInvFlag)
                .setText(R.id.materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.materialGroup, item.materialGroup)
                .setText(R.id.materialUnit,item.unit)
                .setText(R.id.actQuantity,item.actQuantity)
                .setText(R.id.quantity, item.totalQuantity)
                .setText(R.id.inv, item.invCode)
                .setText(R.id.manufacturer,item.manufacturer)
                .setText(R.id.randomQuantity,item.randomQuantity)
                .setText(R.id.qualifiedQuantity,item.qualifiedQuantity)
                .setText(R.id.rustQuantity,item.rustQuantity)
                .setText(R.id.damagedQuantity,item.damagedQuantity)
                .setText(R.id.badQuantity,item.badQuantity)
                .setText(R.id.otherQuantity,item.otherQuantity)
                .setText(R.id.zPackage,item.sapPackage)
                .setText(R.id.qmNum,item.qmNum)
                .setText(R.id.certificate,item.certificate)
                .setText(R.id.manual,item.instructions)
                .setText(R.id.inspectionQuantity,item.inspectionQuantity)
                .setText(R.id.qmCertificate,item.qmCertificate)
                .setText(R.id.claimNum,item.claimNum)
                .setText(R.id.inspectionResult,item.inspectionResult);

    }

    @Override
    public void notifyParentNodeChanged(int childNodePosition, int parentNodePosition) {

    }

    @Override
    public void notifyNodeChanged(int position) {
        RefDetailEntity deleteNode = mVisibleNodes.get(position);
        deleteNode.invCode = "";
        deleteNode.totalQuantity = "";
        deleteNode.manufacturer = "" ;
        deleteNode.randomQuantity = "";
        deleteNode.qualifiedQuantity = "";
        deleteNode.rustQuantity = "";
        deleteNode.damagedQuantity = "";
        deleteNode.badQuantity = "";
        deleteNode.otherQuantity = "";
        deleteNode.sapPackage = "";
        deleteNode.qmNum = "";
        deleteNode.certificate = "";
        deleteNode.instructions = "";
        deleteNode.qmCertificate = "";
        deleteNode.inspectionResult = "";
        notifyItemChanged(position);
    }

    @Override
    protected Map<String, Object> provideExtraData(int position) {
        return null;
    }
}
