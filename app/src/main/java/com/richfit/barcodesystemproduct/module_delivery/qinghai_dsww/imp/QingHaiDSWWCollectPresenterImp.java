package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsww.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module_delivery.basecollect.imp.DSCollectPresenterImp;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.domain.bean.InventoryEntity;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by monday on 2017/3/10.
 */

public class QingHaiDSWWCollectPresenterImp extends DSCollectPresenterImp {

    @Inject
    public QingHaiDSWWCollectPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void getInventoryInfo(String queryType, String workId, String invId, String workCode, String invCode, String storageNum,
                                 String materialNum, String materialId, String location, String batchFlag,
                                 String specialInvFlag, String specialInvNum, String invType) {
        mView = getView();
        RxSubscriber<List<InventoryEntity>> subscriber = null;
        if ("04".equals(queryType)) {
            subscriber = mRepository.getStorageNum(workId, workCode, invId, invCode)
                    .filter(num -> !TextUtils.isEmpty(num))
                    .flatMap(num -> mRepository.getInventoryInfo(queryType, workId, invId,
                            workCode, invCode, num, materialNum, materialId, "", "", batchFlag, location, specialInvFlag, "", invType))
                    .compose(TransformerHelper.io2main())
                    .subscribeWith(new InventorySubscriber(mContext, "正在获取库存"));

        } else {
            subscriber = mRepository.getInventoryInfo(queryType, workId, invId,
                    workCode, invCode, storageNum, materialNum, materialId, "", "", batchFlag, location, specialInvFlag, "", invType)
                    .compose(TransformerHelper.io2main())
                    .subscribeWith(new InventorySubscriber(mContext, "正在获取库存"));
        }
        addSubscriber(subscriber);
    }
}
