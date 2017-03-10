package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.detail;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.ArrayList;
import java.util.List;

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
            mView.setRefreshing(true, "未获取到明细行行号");
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
            //处理父节点的缓存
            cachedEntity.mapExt = UiUtil.copyMap(node.mapExt, cachedEntity.mapExt);
            nodes.add(cachedEntity);
        }
        //生成父节点
        addTreeInfo(nodes);
        return nodes;
    }


    /**
     * 通过refLineId将缓存和原始单据行关联起来
     */
    protected RefDetailEntity getLineDataByRefLineId(RefDetailEntity refLineData, ReferenceEntity cachedRefData) {
        if (refLineData == null) {
            return null;
        }
        return getLineDataByRefLineIdInternal(String.valueOf(refLineData.refDocItem), cachedRefData);
    }
}
