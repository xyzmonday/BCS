package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module.edit.EditActivity;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.SPrefUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.LocationInfoEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2017/3/10.
 */

public class QingHaiWWCDetailPresenterImp extends BasePresenter<QingHaiWWCDetailContract.IQingHaiWWCDetailView>
        implements QingHaiWWCDetailContract.IQingHaiWWCDetailPresenter {

    QingHaiWWCDetailContract.IQingHaiWWCDetailView mView;

    @Inject
    public QingHaiWWCDetailPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getTransferInfo(String refNum, String refCodeId, String bizType, String refType,
                                String moveType, String refLineId, String userId) {
        mView = getView();

        if (TextUtils.isEmpty(refNum) && mView != null) {
            mView.setRefreshing(true, "未获取到单据号");
            return;
        }

        if (TextUtils.isEmpty(refLineId) && mView != null) {
            mView.setRefreshing(true, "未获取到明细行行Id");
            return;
        }

        ResourceSubscriber<List<RefDetailEntity>> subscriber =
                Flowable.zip(mRepository.getReference(refNum, refType, bizType, moveType, refLineId, userId),
                        mRepository.getTransferInfo(refNum, refCodeId, bizType, refType, userId, "", "", "", "")
                                .onErrorReturnItem(new ReferenceEntity()),
                        (refData, cache) -> createNodesByCache(refData, cache))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<List<RefDetailEntity>>() {
                            @Override
                            public void onNext(List<RefDetailEntity> nodes) {
                                if (mView != null) {
                                    mView.showNodes(nodes);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.setRefreshing(true, t.getMessage());
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

    @Override
    public void deleteNode(String lineDeleteFlag, String transId, String transLineId, String locationId,
                           String refType, String bizType, final int position, String companyCode) {
        RxSubscriber<String> subscriber = mRepository.deleteCollectionDataSingle(lineDeleteFlag, transId, transLineId,
                locationId, refType, bizType, "", "", position, companyCode)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext) {
                    @Override
                    public void _onNext(String s) {

                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {

                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.deleteNodeFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.deleteNodeFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.deleteNodeSuccess(position);
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void editNode(ArrayList<String> locations, ReferenceEntity refData, RefDetailEntity node, String companyCode,
                         String bizType, String refType, String subFunName, int position) {
        if (refData != null) {

            Intent intent = new Intent(mContext, EditActivity.class);

            Bundle bundle = new Bundle();
            //该子节点的id
            bundle.putString(Global.EXTRA_REF_LINE_ID_KEY, node.refLineId);
            bundle.putString(Global.EXTRA_LOCATION_ID_KEY, node.locationId);

            bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);
            bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);

            //子节点在明细中的位置
            bundle.putInt(Global.EXTRA_POSITION_KEY, position);

            //入库的子菜单的名称
            bundle.putString(Global.EXTRA_TITLE_KEY, subFunName + "-明细修改");

            //累计数量
            bundle.putString(Global.EXTRA_TOTAL_QUANTITY_KEY, node.totalQuantity);

            //批次
            bundle.putString(Global.EXTRA_BATCH_FLAG_KEY, node.batchFlag);

            //实收数量
            bundle.putString(Global.EXTRA_QUANTITY_KEY, node.quantity);

            intent.putExtras(bundle);
            Activity activity = (Activity) mContext;
            activity.startActivity(intent);

        }
    }
    @Override
    public void submitData2BarcodeSystem(String transId, String bizType, String refType, String userId, String voucherDate,
                                         Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mView = getView();
        RxSubscriber<String> subscriber = Flowable.concat(mRepository.uploadCollectionData("", transId, bizType, refType, -1, voucherDate, "", userId),
                mRepository.transferCollectionData(transId, bizType, refType, userId, voucherDate, flagMap, extraHeaderMap))
                .doOnError(str -> SPrefUtil.saveData(bizType + refType, "0"))
                .doOnComplete(() -> SPrefUtil.saveData(bizType + refType, "1"))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在过账...") {
                    @Override
                    public void _onNext(String message) {
                        if (mView != null) {
                            mView.showTransferedVisa(message);
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
    public void submitData2SAP(String transId, String bizType, String refType, String userId,
                               String voucherDate, Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mView = getView();
        RxSubscriber<String> subscriber = mRepository.transferCollectionData(transId, bizType, refType, Global.USER_ID, voucherDate, flagMap, extraHeaderMap)
                .doOnComplete(() -> SPrefUtil.saveData(bizType + refType, "0"))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<String>(mContext, "正在上传数据...") {
                    @Override
                    public void _onNext(String message) {
                        if (mView != null) {
                            mView.showInspectionNum(message);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.networkConnectError(Global.RETRY_UPLOAD_DATA_ACTION);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null && !TextUtils.isEmpty(message)) {
                            mView.submitSAPFail(message.split("_"));
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.submitSAPFail(new String[]{message});
                        }
                    }

                    @Override
                    public void _onComplete() {
                        if (mView != null) {
                            mView.submitSAPSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void sapUpAndDownLocation(String transId, String bizType, String refType, String userId, String voucherDate, Map<String, Object> flagMap, Map<String, Object> extraHeaderMap, int submitFlag) {

    }

    /**
     * 通过抬头获取的单据数据和缓存数据生成新的单据数据。
     * 注意我们的目的是将这两部分数据完全分离，这样有利于处理。
     * 扩展字段生成的规则：
     * 父节点：原始单据的扩展字段+对应的该行的缓存扩展字段
     * 子节点：该行仓位级别的缓存扩展字段
     *
     * @param refData：塔头获取的原始单据数据
     * @param cache：缓存单据数据
     * @return
     */
    protected List<RefDetailEntity> createNodesByCache(ReferenceEntity refData, ReferenceEntity cache) {
        ArrayList<RefDetailEntity> nodes = new ArrayList<>();
        //第一步，将原始单据中的行明细赋值新的父节点中
        List<RefDetailEntity> list = refData.billDetailList;
        if (cache == null || cache.billDetailList == null || cache.billDetailList.size() == 0) {
            return list;
        }
        for (RefDetailEntity node : list) {
            //获取缓存中的明细，如果该行明细没有缓存，那么该行明细仅仅赋值原始单据信息
            RefDetailEntity cachedEntity = getLineDataByRefLineId(node, cache);
            if (cachedEntity == null)
                cachedEntity = new RefDetailEntity();
            //将原始单据的物料信息赋值给缓存
            cachedEntity.lineNum = node.lineNum;
            cachedEntity.materialNum = node.materialNum;
            cachedEntity.materialId = node.materialId;
            cachedEntity.materialDesc = node.materialDesc;
            cachedEntity.materialGroup = node.materialGroup;
            cachedEntity.unit = node.unit;
            cachedEntity.actQuantity = node.actQuantity;
            //注意refDoc和refDocItem在原始单据中
            cachedEntity.refDoc = node.refDoc;
            cachedEntity.refDocItem = node.refDocItem;

            //将仓位级别的数据保存到明细行级别中
            List<LocationInfoEntity> locationList = cachedEntity.locationList;
            if (locationList != null && locationList.size() > 0) {
                for (LocationInfoEntity loc : locationList) {
                    //仓位级别的数据
                    cachedEntity.transId = loc.transId;
                    cachedEntity.location = loc.location;
                    cachedEntity.quantity = loc.quantity;
                    cachedEntity.transLineId = loc.transLineId;
                    cachedEntity.batchFlag = loc.batchFlag;
                    cachedEntity.locationId = loc.id;
                    cachedEntity.mapExt = UiUtil.copyMap(cachedEntity.mapExt, loc.mapExt);
                }
            }
            //处理父节点的缓存
            cachedEntity.mapExt = UiUtil.copyMap(node.mapExt, cachedEntity.mapExt);
            nodes.add(cachedEntity);
        }
        return nodes;
    }


    /**
     * 通过refDocItem将缓存和原始单据行关联起来
     */
    @Override
    protected RefDetailEntity getLineDataByRefLineId(RefDetailEntity refLineData, ReferenceEntity cachedRefData) {
        if (refLineData == null) {
            return null;
        }

        final String refDocItem = String.valueOf(refLineData.refDocItem);
        if ("null".equals(refDocItem))
            return null;
        //通过refDocItem匹配出缓存中的明细行
        List<RefDetailEntity> detail = cachedRefData.billDetailList;
        for (RefDetailEntity entity : detail) {
            if (refDocItem.equals(String.valueOf(entity.refDocItem))) {
                return entity;
            }
        }
        return null;
    }
}
