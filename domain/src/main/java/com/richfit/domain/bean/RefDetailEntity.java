package com.richfit.domain.bean;

import java.util.List;
import java.util.Map;

/**
 * 单据明细实体类
 */

public class RefDetailEntity extends TreeNode {

    /*单据号*/
    public String recordNum;
    /*在验收中，该行是否已经验收*/
    public boolean isChecked;
    /*行明细id，同时也是本地数据行明细的id(主键)*/
    public String refLineId;
    /*缓存头id*/
    public String transId;
    /*缓存行id*/
    public String transLineId;
    /*行号*/
    public String lineNum;
    /*物料id*/
    public String materialId;
    /*物料号*/
    public String materialNum;
    /*物料描述*/
    public String materialDesc;
    /*物料组*/
    public String materialGroup;
    /*采购订单号*/
    public String poNum;
    /*采购订单行号*/
    public String poLineNum;
    /*计量单位*/
    public String unit;
    /*实收数量*/
    public String actQuantity;
    /*到货数量*/
    public String arrivalQuantity;
    /*批次*/
    public String batchFlag;
    /*验收日期*/
    public String inspectionDate;
    /*验收结果*/
    public String inspectionResult;
    /*检验标准*/
    public String inspectionStandard;
    /*特殊库存标识*/
    public String specialInvFlag;
    /*该行是否验收标识*/
    public String lineInspectFlag;
    /*单据数量*/
    public String orderQuantity;
    /*备注*/
    public String remark;
    /*工厂*/
    public String workId;
    public String workCode;
    public String workName;
    /*库存地点*/
    public String invId;
    public String invCode;
    public String invName;

    public String photoFlag;

    /*累计数量*/
    public String totalQuantity;

    /*仓位*/
    public String location;

    /*用户录入的数量*/
    public String quantity;

    /*仓位级别缓存的id*/
    public String locationId;

    /*供应商*/
    public String supplierCode;
    public String supplierName;

    /*单价*/
    public String price;

    /*接收仓位*/
    public String recLocation;

    /*接收批次*/
    public String recBatchFlag;

    /*仓位信息*/
    public List<LocationInfoEntity> locationList;

    /*单据行的额外字段数据*/
    public Map<String, Object> mapExt;

    @Override
    public String toString() {
        return "RefDetailEntity{" +
                "recordNum='" + recordNum + '\'' +
                ", isChecked=" + isChecked +
                ", refLineId='" + refLineId + '\'' +
                ", transId='" + transId + '\'' +
                ", transLineId='" + transLineId + '\'' +
                ", lineNum='" + lineNum + '\'' +
                ", materialId='" + materialId + '\'' +
                ", materialNum='" + materialNum + '\'' +
                ", materialDesc='" + materialDesc + '\'' +
                ", materialGroup='" + materialGroup + '\'' +
                ", poNum='" + poNum + '\'' +
                ", poLineNum='" + poLineNum + '\'' +
                ", unit='" + unit + '\'' +
                ", actQuantity='" + actQuantity + '\'' +
                ", arrivalQuantity='" + arrivalQuantity + '\'' +
                ", batchFlag='" + batchFlag + '\'' +
                ", inspectionDate='" + inspectionDate + '\'' +
                ", inspectionResult='" + inspectionResult + '\'' +
                ", inspectionStandard='" + inspectionStandard + '\'' +
                ", specialInvFlag='" + specialInvFlag + '\'' +
                ", lineInspectFlag='" + lineInspectFlag + '\'' +
                ", orderQuantity='" + orderQuantity + '\'' +
                ", remark='" + remark + '\'' +
                ", workId='" + workId + '\'' +
                ", workCode='" + workCode + '\'' +
                ", workName='" + workName + '\'' +
                ", invId='" + invId + '\'' +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", photoFlag='" + photoFlag + '\'' +
                ", totalQuantity='" + totalQuantity + '\'' +
                ", location='" + location + '\'' +
                ", quantity='" + quantity + '\'' +
                ", locationId='" + locationId + '\'' +
                ", supplierCode='" + supplierCode + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", price='" + price + '\'' +
                ", recLocation='" + recLocation + '\'' +
                ", recBatchFlag='" + recBatchFlag + '\'' +
                ", locationList=" + locationList +
                ", mapExt=" + mapExt +
                '}';
    }
}
