package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_103.imp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.barcodesystemproduct.module_acceptstore.basedetail.imp.ASDetailPresenterImp;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiAS103DetailPresenterImp extends ASDetailPresenterImp {

    @Inject
    public QingHaiAS103DetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void editNode(ArrayList<String> locations, ReferenceEntity refData, RefDetailEntity node, String companyCode,
                         String bizType, String refType, String subFunName, int position) {

        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle bundle = new Bundle();
        //该子节点的id
        bundle.putString(Global.EXTRA_REF_LINE_ID_KEY, node.refLineId);
        bundle.putString(Global.EXTRA_LOCATION_ID_KEY, node.locationId);

        //入库子菜单类型
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);

        bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);

        //父节点的位置
        bundle.putInt(Global.EXTRA_POSITION_KEY, position);

        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, subFunName + "-明细修改");

        //库存地点
        bundle.putString(Global.EXTRA_INV_ID_KEY, node.invId);
        bundle.putString(Global.EXTRA_INV_CODE_KEY, node.invCode);

        //累计数量
        bundle.putString(Global.EXTRA_TOTAL_QUANTITY_KEY, node.totalQuantity);

        //批次
        bundle.putString(Global.EXTRA_BATCH_FLAG_KEY, node.batchFlag);

        //上架仓位
        bundle.putString(Global.EXTRA_LOCATION_KEY, node.location);

        //实收数量
        bundle.putString(Global.EXTRA_QUANTITY_KEY, node.quantity);

        //发出仓位集合
        bundle.putStringArrayList(Global.EXTRA_LOCATION_LIST_KEY, locations);

        //子节点的额外字段的数据
        bundle.putSerializable(Global.LOCATION_EXTRA_MAP_KEY, (Serializable) node.mapExt);

        intent.putExtras(bundle);
        Activity activity = (Activity) mContext;
        activity.startActivity(intent);
    }

    @Override
    public void submitData2BarcodeSystem(String transId, String bizType, String refType, String userId,
                                         String voucherDate, Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mView = getView();
        RxSubscriber<String> subscriber =
                Flowable.concat(mRepository.uploadCollectionData("", transId, bizType, refType, -1, voucherDate, "", ""),
                        mRepository.transferCollectionData(transId, bizType, refType, userId, voucherDate, flagMap, extraHeaderMap))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<String>(mContext, "正在过账数据...") {
                            @Override
                            public void _onNext(String s) {
                                if (mView != null) {
                                    mView.showTransferedVisa(s);
                                }
                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.networkConnectError(Global.RETRY_TRANSFER_DATA_ACTION);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if (mView != null) {
                                    mView.submitBarcodeSystemFail(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.submitBarcodeSystemFail(message);
                                }
                            }

                            @Override
                            public void _onComplete() {
                                if (mView != null) {
                                    mView.submitBarcodeSystemSuccess();
                                }
                            }
                        });
        addSubscriber(subscriber);
    }

    @Override
    public void getTransferInfo(final ReferenceEntity refData, String refCodeId, String bizType, String refType) {
        mView = getView();
        ResourceSubscriber<ArrayList<RefDetailEntity>> subscriber = mRepository.getTransferInfo("", refCodeId, bizType, refType,
                "", "", "", "", "")
                .zipWith(Flowable.just(refData), (cache, data) -> createNodesByCache(data, cache))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<ArrayList<RefDetailEntity>>() {
                    @Override
                    public void onNext(ArrayList<RefDetailEntity> nodes) {
                        if (mView != null) {
                            mView.showNodes(nodes);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.setRefreshing(true, t.getMessage());
                            //展示抬头获取的数据，没有缓存
                            mView.showNodes(refData.billDetailList);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.setRefreshing(true, "获取明细缓存成功");
                        }
                    }
                });
        addSubscriber(subscriber);
    }


    /**
     * 对于103入库，有参考但是不是父子节点的结构，所以需要重写createNodesByCache
     * 方法
     *
     * @param refData：塔头获取的原始单据数据
     * @param cache：缓存单据数据
     * @return
     */
    @Override
    protected ArrayList<RefDetailEntity> createNodesByCache(ReferenceEntity refData, ReferenceEntity cache) {
        ArrayList<RefDetailEntity> nodes = new ArrayList<>();
        //第一步，将原始单据中的行明细赋值新的父节点中
        List<RefDetailEntity> list = refData.billDetailList;
        for (RefDetailEntity node : list) {
            //获取缓存中的明细，如果该行明细没有缓存，那么该行明细仅仅赋值原始单据信息
            RefDetailEntity cacheEntity = getLineDataByRefLineId(node.refLineId, cache);
            if (cacheEntity == null)
                cacheEntity = new RefDetailEntity();

            cacheEntity.lineNum = node.lineNum;
            cacheEntity.materialNum = node.materialNum;
            cacheEntity.materialId = node.materialId;
            cacheEntity.materialDesc = node.materialDesc;
            cacheEntity.materialGroup = node.materialGroup;
            cacheEntity.actQuantity = node.actQuantity;
            cacheEntity.workCode = node.workCode;

            List<LocationInfoEntity> locationList = cacheEntity.locationList;
            if (locationList != null && locationList.size() > 0) {
                for (LocationInfoEntity loc : locationList) {
                    //仓位级别的数据
                    cacheEntity.transId = loc.transId;
                    cacheEntity.location = loc.location;
                    cacheEntity.quantity = loc.quantity;
                    cacheEntity.transLineId = loc.transLineId;
                    cacheEntity.locationId = loc.id;
                    cacheEntity.mapExt = UiUtil.copyMap(cacheEntity.mapExt, loc.mapExt);
                }
            }
            //处理父节点的缓存
            cacheEntity.mapExt = UiUtil.copyMap(node.mapExt, cacheEntity.mapExt);
            nodes.add(cacheEntity);
        }
        return nodes;
    }


}
