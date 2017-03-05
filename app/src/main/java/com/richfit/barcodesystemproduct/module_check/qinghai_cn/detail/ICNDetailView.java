package com.richfit.barcodesystemproduct.module_check.qinghai_cn.detail;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.ReferenceEntity;

/**
 * Created by monday on 2016/12/6.
 */

public interface ICNDetailView extends BaseView {

    /**
     * 加载整单缓存数据成功后显示明细
     *
     * @param refData
     * @param pageNum
     */
    void showCheckInfo(ReferenceEntity refData, int pageNum);

    void loadCheckInfoFail(String message);


    /**
     * 删除子节点成功
     *
     * @param position：节点在明细列表的位置
     */
    void deleteNodeSuccess(int position);

    /**
     * 删除子节点失败
     *
     * @param message
     */
    void deleteNodeFail(String message);

}
