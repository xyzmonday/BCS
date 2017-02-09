package com.richfit.common_lib.IInterface;

/**
 * fragment的状态.包括抬头，数据明细，数据采三种状态。
 * Created by monday on 2016/11/16.
 */

public interface IFragmentState<T> {

    /**
     * 抬头界面的处理。目前的需求仅仅是信息查询中用到
     */
    boolean checkDataBeforeOperationOnHeader();

    void operationOnHeader(final String companyCode);

    boolean checkDataBeforeOperationOnDetail();
    /**
     * 在数据明细界面，不同的功能模块需要操作不同。比如验收只需要过账，
     * 而入库需要过账和数据上传。
     * @param companyCode:菜单名称列表
     */
    void showOperationMenuOnDetail(final String companyCode);

    /**
     * 明细界面子节点删除
     * @param node
     * @param position
     */
    void deleteNode(T node, int position);

    /**
     * 明细界面子节点编辑(修改)
     * @param node
     * @param position
     */
    void editNode(T node, int position);
    /**
     * 数据采集界面，用户保存数据前，检查数据是否合格
     */
    boolean checkCollectedDataBeforeSave();

    /**
     * 数据采集界面，保存用户采集的数据
     */
    void saveCollectedData();

    /**
     * 数据采集界面，显示底部对话框。一般来说，只需要弹出一个对话框即可，
     * 用户自己判断是否需要保存本次采集的数据。但是对于特殊的需求，比如说：
     * 验收功能的数据采集界面需要根据不同的需要弹出不同的对话框。
     *
     * @param companyCode:地区工厂的编码
     */
    void showOperationMenuOnCollection(String companyCode);

    /**
     * 条码扫描处理
     * @param type
     * @param list
     */
    void handleBarCodeScanResult(String type, String[] list);

}
