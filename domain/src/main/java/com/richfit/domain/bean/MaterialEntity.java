package com.richfit.domain.bean;

/**
 * 物料实体类
 * Created by monday on 2017/2/7.
 */

public class MaterialEntity {
    public String id;
    public String materialNum;
    public String materialDesc;
    public String unit;
    public String batchFlag;
    public String materialGroup;

    @Override
    public String toString() {
        return "MaterialEntity{" +
                "id='" + id + '\'' +
                ", materialNum='" + materialNum + '\'' +
                ", materialDesc='" + materialDesc + '\'' +
                ", unit='" + unit + '\'' +
                ", batchFlag='" + batchFlag + '\'' +
                ", materialGroup='" + materialGroup + '\'' +
                '}';
    }
}
