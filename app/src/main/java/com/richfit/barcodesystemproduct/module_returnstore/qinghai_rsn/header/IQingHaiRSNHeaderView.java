package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.header;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.WorkEntity;

import java.util.List;

/**
 * Created by monday on 2017/3/2.
 */

public interface IQingHaiRSNHeaderView extends BaseView {
    void showWorks(List<WorkEntity> works);

    void loadWorksFail(String message);

    void showAutoCompleteList(List<String> suppliers);

    void loadAutoCompleteFail(String message);

    void deleteCacheSuccess(String message);

    void deleteCacheFail(String message);
}
