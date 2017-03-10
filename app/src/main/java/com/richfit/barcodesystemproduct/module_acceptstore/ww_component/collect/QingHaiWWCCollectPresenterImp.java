package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.collect;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.InventoryEntity;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by monday on 2017/3/10.
 */

public class QingHaiWWCCollectPresenterImp extends BasePresenter<QingHaiWWCCollectContract.QingHaiWWCCollectView>
        implements QingHaiWWCCollectContract.QingHaiWWCCollectPresenter {

    QingHaiWWCCollectContract.QingHaiWWCCollectView mView;

    @Inject
    public QingHaiWWCCollectPresenterImp(@ContextLife("Activity") Context context) {
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
                            workCode, invCode, num, materialNum, materialId, "", "", batchFlag, location, specialInvFlag, specialInvNum, invType))
                    .compose(TransformerHelper.io2main())
                    .subscribeWith(new InventorySubscriber(mContext, "正在获取库存"));

        } else {
            subscriber = mRepository.getInventoryInfo(queryType, workId, invId,
                    workCode, invCode, storageNum, materialNum, materialId, "", "", batchFlag, location, specialInvFlag, specialInvNum, invType)
                    .compose(TransformerHelper.io2main())
                    .subscribeWith(new InventorySubscriber(mContext, "正在获取库存"));
        }
        addSubscriber(subscriber);
    }

    protected class InventorySubscriber extends RxSubscriber<List<InventoryEntity>> {

        public InventorySubscriber(Context context, String msg) {
            super(context, msg);
        }

        @Override
        public void _onNext(List<InventoryEntity> list) {
            if (mView != null) {
                mView.showInventory(list);
            }
        }

        @Override
        public void _onNetWorkConnectError(String message) {
            if (mView != null) {
                mView.networkConnectError(Global.RETRY_LOAD_INVENTORY_ACTION);
            }
        }

        @Override
        public void _onCommonError(String message) {
            if (mView != null) {
                mView.loadInventoryFail(message);
            }
        }

        @Override
        public void _onServerError(String code, String message) {
            if (mView != null) {
                mView.loadInventoryFail(message);
            }
        }

        @Override
        public void _onComplete() {

        }
    }

}
