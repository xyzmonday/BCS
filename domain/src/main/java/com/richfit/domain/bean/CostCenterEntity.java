package com.richfit.domain.bean;

/**
 * Created by monday on 2017/2/27.
 */

public class CostCenterEntity {

    public String id;
    public String costCenterCode;
    public String costCenterDesc;


    @Override
    public String toString() {
        return "CostCenterEntity{" +
                "id='" + id + '\'' +
                ", costCenterCode='" + costCenterCode + '\'' +
                ", costCenterDesc='" + costCenterDesc + '\'' +
                '}';
    }
}
