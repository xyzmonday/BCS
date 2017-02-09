package com.richfit.domain.bean;

/**
 * 供应商
 * Created by monday on 2016/7/21.
 */
public class SupplierEntity {
    public String supplierId;
    public String supplierCode;
    public String supplierName;

    @Override
    public String toString() {
        return "SupplierEntity{" +
                "supplierId='" + supplierId + '\'' +
                ", supplierCode='" + supplierCode + '\'' +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}
