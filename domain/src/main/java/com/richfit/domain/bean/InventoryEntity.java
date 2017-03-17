package com.richfit.domain.bean;

/**
 * 库存信息实体类
 * Created by monday on 2016/5/28.
 */
public class InventoryEntity extends TreeNode{
    public String checkLineId;
    public boolean isChecked;
    public String rowId;
    public String id;
    public String transId;
    public String storageNum;
    public String workId;
    public String workCode;
    public String invId;
    public String invCode;
    public String invName;
    public String location;
    public String batchFlag;
    public String materialId;
    public String materialNum;
    public String materialDesc;
    public String materialGroup;
    public String materialUnit;
    public String lineNum;
    public String unit;
    public String invQuantity;
    public String quantity;
    public String supplierId;
    public String supplierNum;
    public String totalQuantity;
    public String workName;
    /**
     * 盘点凭证
     */
    public String checkVisa;
    /**
     * 库存类型
     */
    public String inventoryType;
    /**
     * 特殊库存标识
     */
    public String specialInvFlag;
    /**
     * 特殊库存编号
     */
    public String specialInvNum;
    public String newFlag;//新增库存标识


    @Override
    public String toString() {
        return "InventoryEntity{" +
                "checkLineId='" + checkLineId + '\'' +
                ", isChecked=" + isChecked +
                ", rowId='" + rowId + '\'' +
                ", id='" + id + '\'' +
                ", newFlag='" + newFlag + '\'' +
                ", transId='" + transId + '\'' +
                ", storageNum='" + storageNum + '\'' +
                ", workId='" + workId + '\'' +
                ", workCode='" + workCode + '\'' +
                ", invId='" + invId + '\'' +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", location='" + location + '\'' +
                ", batchFlag='" + batchFlag + '\'' +
                ", materialId='" + materialId + '\'' +
                ", materialNum='" + materialNum + '\'' +
                ", materialDesc='" + materialDesc + '\'' +
                ", materialGroup='" + materialGroup + '\'' +
                ", materialUnit='" + materialUnit + '\'' +
                ", lineNum='" + lineNum + '\'' +
                ", unit='" + unit + '\'' +
                ", invQuantity='" + invQuantity + '\'' +
                ", quantity='" + quantity + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", supplierNum='" + supplierNum + '\'' +
                ", totalQuantity='" + totalQuantity + '\'' +
                ", workName='" + workName + '\'' +
                ", checkVisa='" + checkVisa + '\'' +
                ", inventoryType='" + inventoryType + '\'' +
                ", specialInvFlag='" + specialInvFlag + '\'' +
                ", specialInvNum='" + specialInvNum + '\'' +
                '}';
    }
}
