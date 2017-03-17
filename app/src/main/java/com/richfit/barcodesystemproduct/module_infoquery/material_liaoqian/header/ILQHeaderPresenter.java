package com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.header;

import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2017/3/16.
 */

public interface ILQHeaderPresenter extends IPresenter<ILQHeaderView> {
    void getMaterialInfo(String queryType,String materialNum);
}
