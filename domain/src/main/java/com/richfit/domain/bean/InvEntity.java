package com.richfit.domain.bean;

/**
 * 库存地点实体类
 * Created by monday on 2016/6/18.
 */
public class InvEntity {

    public boolean isExtraInv;
    public String invCode;
    public String invName;
    public String invId;


    @Override
    public String toString() {
        return "InvEntity{" +
                "isExtraInv=" + isExtraInv +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", invId='" + invId + '\'' +
                '}';
    }
}
