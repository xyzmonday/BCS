package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.collect;

import android.support.annotation.NonNull;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monday on 2017/2/28.
 */

public interface IQingHaiAOCollectView extends BaseView {

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
     * 通过刷新界面
     * @param cache
     * @param batchFlag
     * @param location
     */
    void onBindCache(RefDetailEntity cache, String batchFlag, String location);
    /**
     * 未获取到缓存
     * @param message
     */
    void loadCacheFail(String message);
    /**
     * 为数据采集界面的UI绑定数据
     */
    void bindCommonCollectUI();

    /**
     * 保存单条采集验收数据
     */
    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);

    void showInvs(List<InvEntity> invs);
    void loadInvsFail(String message);
}
