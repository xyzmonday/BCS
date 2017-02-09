package com.richfit.barcodesystemproduct.base;

import com.richfit.common_lib.IInterface.IView;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2016/11/25.
 */

public interface BaseView extends IView {

    /**
     * 读取配置信息。一般对于有参考的功能模块来说，抬头界面读取的是configType=0的配置信息；
     * 数据明细界面读取configType=1,2的配置信息，数据采集界面读取configType=3,4的配置信息；
     * 对于无参考的,和不需要录入仓位的功能模块来说，读取的配置信息不变，但是对于明细界面没有父子节点
     * 以及数据采集界面不需要录入仓位的，需要将配置信息进一步处理。
     * @param configs：配置信息列表，位置索引按照传入的configType的顺序分别保存配置信息。
     */
    void readConfigsSuccess(List<ArrayList<RowConfig>> configs);

    void readConfigsFail(String message);

    void readConfigsComplete();

    /**
     * 通过字典读取额外字段数据源成功.
     * @param datas
     */
    void readExtraDictionarySuccess(Map<String, Object> datas);

    void readExtraDictionaryFail(String message);

    void readExtraDictionaryComplete();

}
