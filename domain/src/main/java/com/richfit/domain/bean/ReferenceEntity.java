package com.richfit.domain.bean;


import java.util.List;
import java.util.Map;

/**
 * 单据信息
 */
public class ReferenceEntity {
    /*单据id，同时也是本地数据抬的id(主键)*/
    public String checkId;
    public String refCodeId;

    /*单据号*/
    public String checkNum;
    public String recordNum;
    /*验收抬头缓存标识*/
    public String tempFlag ;
    /*盘点缓存*/
    public String checkFlag;
    /*缓存抬头id*/
    public String transId;
    public String transLineId;
    /*单据类型*/
    public String refType;
    /*移动类型*/
    public String moveType;
    /*业务类型*/
    public String bizType;
    /*工厂*/
    public String workId;
    public String workCode;
    public String workName;

    /*接收工厂*/
    public String recWorkId;
    public String recWorkCode;
    public String recWorkName;

    /*库存地点*/
    public String invId;
    public String invCode;
    public String invName;
    /*库存类型*/
    public String invType;

    /*接收库存地点*/
    public String recInvId;
    public String recInvCode;
    public String recInvName;

    /*创建人*/
    public String recordCreator;
    /*采购号*/
    public String poNum;

    /*供应商*/
    public String supplierDesc;
    public String supplierNum;
    public String supplierId;

    /*过账日期*/
    public String voucherDate;

    /*项目编号*/
    public String projectNum;

    /*网络编号*/
    public String netNum;

    /*客户*/
    public String customer;

    /*成本中心*/
    public String costCenter;

    /*代管料出库原因*/
    public String dsReason;

    /*代管料出库类型*/
    public String asType;

    /*盘点状态*/
    public String status;

    public String storageNum;

    /*盘点总条数*/
    public int totalCount;
    /*验收类型*/
    public int inspectionType;
    /*行明细*/
    public List<RefDetailEntity> billDetailList;
    /*库存明细*/
    public List<InventoryEntity> checkList;
    /*额外字段的数据*/
    public Map<String, Object> mapExt;


    public String checkLevel;
    public String specialFlag;

    @Override
    public String toString() {
        return "ReferenceEntity{" +
                "checkId='" + checkId + '\'' +
                ", refCodeId='" + refCodeId + '\'' +
                ", checkNum='" + checkNum + '\'' +
                ", recordNum='" + recordNum + '\'' +
                ", tempFlag='" + tempFlag + '\'' +
                ", checkFlag='" + checkFlag + '\'' +
                ", transId='" + transId + '\'' +
                ", transLineId='" + transLineId + '\'' +
                ", refType='" + refType + '\'' +
                ", moveType='" + moveType + '\'' +
                ", bizType='" + bizType + '\'' +
                ", workId='" + workId + '\'' +
                ", workCode='" + workCode + '\'' +
                ", workName='" + workName + '\'' +
                ", recWorkId='" + recWorkId + '\'' +
                ", recWorkCode='" + recWorkCode + '\'' +
                ", recWorkName='" + recWorkName + '\'' +
                ", invId='" + invId + '\'' +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", invType='" + invType + '\'' +
                ", recInvId='" + recInvId + '\'' +
                ", recInvCode='" + recInvCode + '\'' +
                ", recInvName='" + recInvName + '\'' +
                ", recordCreator='" + recordCreator + '\'' +
                ", poNum='" + poNum + '\'' +
                ", supplierDesc='" + supplierDesc + '\'' +
                ", supplierNum='" + supplierNum + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", voucherDate='" + voucherDate + '\'' +
                ", projectNum='" + projectNum + '\'' +
                ", netNum='" + netNum + '\'' +
                ", customer='" + customer + '\'' +
                ", costCenter='" + costCenter + '\'' +
                ", dsReason='" + dsReason + '\'' +
                ", asType='" + asType + '\'' +
                ", status='" + status + '\'' +
                ", storageNum='" + storageNum + '\'' +
                ", totalCount='" + totalCount + '\'' +
                ", inspectionType=" + inspectionType +
                ", billDetailList=" + billDetailList +
                ", checkList=" + checkList +
                ", mapExt=" + mapExt +
                ", checkLevel='" + checkLevel + '\'' +
                ", specialFlag='" + specialFlag + '\'' +
                '}';
    }
}
