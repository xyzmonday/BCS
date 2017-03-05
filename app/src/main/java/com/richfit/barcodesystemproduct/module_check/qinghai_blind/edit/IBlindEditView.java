package com.richfit.barcodesystemproduct.module_check.qinghai_blind.edit;


import com.richfit.barcodesystemproduct.base.BaseView;

/**
 * Created by monday on 2016/12/6.
 */

public interface IBlindEditView extends BaseView {

    void saveCheckDataSuccess();

    void saveCheckDataFail(String message);
}
