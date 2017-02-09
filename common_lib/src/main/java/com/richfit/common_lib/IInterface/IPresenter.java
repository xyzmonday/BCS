package com.richfit.common_lib.IInterface;

import com.richfit.domain.bean.RowConfig;

import java.util.List;

/**
 * mvp的presenter层
 * Created by monday on 2016/9/30.
 */

public interface IPresenter<T extends IView> {
    /**
     * 绑定View
     * @param view
     */
    void attachView(T view);
    void detachView();

    /**
     * 读取额外字段的配置信息。
     * @param companyCode
     * @param bizType
     * @param configTypes:配置信息的类型
     */
    void readExtraConfigs(String companyCode, String bizType, String refType, String... configTypes);

    void readExtraDataSourceDictionary(List<RowConfig> configs);
}
