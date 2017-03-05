package com.richfit.barcodesystemproduct.module_delivery.qinghai_dsn.header;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.WorkEntity;

import java.util.List;

/**
 * 青海出库无参考抬头界面
 * Created by monday on 2017/2/23.
 */

public interface IQingHaiDSNHeaderView extends BaseView {
    void showWorks(List<WorkEntity> works);
    void loadWorksFail(String message);

    void showAutoCompleteList(List<String> suppliers);
    void loadAutoCompleteFail(String message);

    void deleteCacheSuccess(String message);
    void deleteCacheFail(String message);

}
