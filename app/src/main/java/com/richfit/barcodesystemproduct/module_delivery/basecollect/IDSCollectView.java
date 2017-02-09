package com.richfit.barcodesystemproduct.module_delivery.basecollect;

import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monday on 2016/11/19.
 */

public interface IDSCollectView extends BaseView {

    /**
     * 获取匹配的物料信息
     *
     * @param materialNum：物料号
     * @param batchFlag：批次
     */
    void loadMaterialInfo(@NonNull String materialNum, @NonNull String batchFlag);
    /**
     * 初始化单据行适配器
     */
    void setupRefLineAdapter(ArrayList<String> refLines);

    /**
     * 为数据采集界面的UI绑定数据
     */
    void bindCommonCollectUI();

    /**
     * 获取库存地点列表失败
     * @param message
     */
    void loadInvFail(String message);
    /**
     * 获取库存地点列表成功
     * @param list
     */
    void showInvs(ArrayList<InvEntity> list);


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

    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);
}
