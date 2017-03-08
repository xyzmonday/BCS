package com.richfit.barcodesystemproduct.module_check.qinghai_blind.detail;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.InventoryEntity;

/**
 * Created by monday on 2016/12/6.
 */

public interface IBlindDetailPresenter extends IPresenter<IBlindDetailView> {

    /**
     * 获取整单缓存
     *
     * @param checkId:盘点头ID
     * @param materialNum:物资编码   可输 可不输
     * @param location:仓位号       可输 可不输
     * @param isPageQuery:是否分页查询 是的话赋值"Y"
     * @param pageNum:页码
     * @param pageSize:每页多少行
     */
    void getCheckTransferInfo(String checkId, String materialNum, String location,
                              String isPageQuery, int pageNum, int pageSize,String bizType);

    /**
     * 删除单条缓存数据
     *
     * @param checkId：抬头id
     * @param checkLineId：行id
     * @param userId：用户id
     * @param position：节点在列表的位置
     */
    void deleteNode(String checkId, String checkLineId, String userId, int position,String bizType);

    /**
     * 修改子节点
     *
     * @param node：需要修改的子节点
     * @param ：subFunName子功能编码
     */
    void editNode(InventoryEntity node, String companyCode, String moduleCode,
                  String subFunCode, String subFunName);

    /**
     * 过账本次盘点
     * @param checkId
     */
    void transferCheckData(String checkId,String userId,String bizType);
    /**
     * 数据提交到sap后，从数据明细界面跳转到抬头界面
     *
     * @param position
     */
    void showHeadFragmentByPosition(int position);
}
