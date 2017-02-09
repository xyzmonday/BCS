package com.richfit.barcodesystemproduct.module_acceptstore.basecollect;

import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;

/**
 * Created by monday on 2016/11/15.
 */

public interface IASCollectView extends BaseView {

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

    void checkLocationFail(String message);
    void checkLocationSuccess(String batchFlag, String location);

    /**
     * 通过缓存刷新界面
     * @param cache
     * @param batchFlag
     * @param location
     */
    void onBindCache(RefDetailEntity cache, String batchFlag, String location);

    /**
     * 获取缓存成功
     */
    void loadCacheSuccess();

    /**
     * 未获取到缓存
     * @param message
     */
    void loadCacheFail(String message);

    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);

}
