package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.SimpleEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;

/**
 * Created by monday on 2016/11/16.
 */

public interface IASNHeaderView extends BaseView {
    /**
     * 显示工厂列表
     */
    void showWorks(ArrayList<WorkEntity> works);

    /**
     * 获取工厂列表失败
     */
    void loadWorksFail(String message);

    /**
     * 获取工厂完成
     */
    void loadWorksComplete();

    /**
     * 显示移动类型列表
     */
    void showMoveTypes(ArrayList<String> moveTypes);

    /**
     * 获取移动类型失败
     */
    void loadMoveTypesFail(String message);

    /**
     * 显示供应商列表
     */
    void showSuppliers(ArrayList<SimpleEntity> suppliers);

    /**
     * 获取供应商失败
     */
    void loadSuppliersFail(String message);

    /**
     * 删除缓存
     * @param message
     */
    void deleteCacheSuccess(String message);
    void deleteCacheFail(String message);
}
