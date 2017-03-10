package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.collect;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * Created by monday on 2017/3/10.
 */

public interface QingHaiWWCCollectContract {
    interface QingHaiWWCCollectView extends BaseView {
        /**
         * 初始化单据行适配器
         */
        void setupRefLineAdapter();
        /**
         * 为数据采集界面的UI绑定数据
         */
        void bindCommonCollectUI();

        /**
         * 显示库存
         * @param list
         */
        void showInventory(List<InventoryEntity> list);
        void loadInventoryFail(String message);

        /**
         * 获取缓存成功
         * @param cache
         * @param batchFlag
         * @param location
         */
        void onBindCache(RefDetailEntity cache, String batchFlag, String location);
        void loadCacheSuccess();
        void loadCacheFail(String message);
    }

    interface QingHaiWWCCollectPresenter extends IPresenter<QingHaiWWCCollectView> {
        /**
         * 获取库存信息
         * @param workId:工厂id
         * @param invId：库存地点id
         * @param materialId：物料id
         * @param location：仓位
         * @param batchFlag:批次
         * @param invType：库存类型
         */
        void getInventoryInfo(String queryType, String workId, String invId, String workCode,String invCode,
                              String storageNum,String materialNum,String materialId, String location, String batchFlag,
                              String specialInvFlag,String specialInvNum,String invType);
    }
}
