package com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.header;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.MaterialEntity;

/**
 * Created by monday on 2017/3/16.
 */

public interface ILQHeaderView extends BaseView {
    void querySuccess(MaterialEntity entity);
    void queryFail(String message);
}
