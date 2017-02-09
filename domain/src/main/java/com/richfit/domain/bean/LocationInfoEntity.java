package com.richfit.domain.bean;

import java.util.Map;

/**
 * 仓位相关的实体类
 *
 * @author HM
 */

public class LocationInfoEntity {

    /*保存的id*/
    public String id;
    /*头ID*/
    public String transId;
    /*行ID*/
    public String transLineId;
    /*仓位号*/
    public String location;
    /*批次*/
    public String batchFlag;
    /*数量*/
    public String quantity;
    /*接收仓位*/
    public String recLocation;
    /*接收批次*/
    public String recBatchFlag;

    /*仓位的额外字段数据*/
    public Map<String, Object> mapExt;

    @Override
    public String toString() {
        return "LocationInfoEntity{" +
                "id='" + id + '\'' +
                ", transId='" + transId + '\'' +
                ", transLineId='" + transLineId + '\'' +
                ", location='" + location + '\'' +
                ", batchFlag='" + batchFlag + '\'' +
                ", quantity='" + quantity + '\'' +
                ", recLocation='" + recLocation + '\'' +
                ", recBatchFlag='" + recBatchFlag + '\'' +
                ", mapExt=" + mapExt +
                '}';
    }
}
