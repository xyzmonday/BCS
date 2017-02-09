package com.richfit.domain.bean;

/**
 * 工厂实体类
 * Created by monday on 2016/6/18.
 */
public class WorkEntity {

    public String workId;
    public String workName;
    public String workCode;


    @Override
    public String toString() {
        return "WorkEntity{" +
                "workId='" + workId + '\'' +
                ", workName='" + workName + '\'' +
                ", workCode='" + workCode + '\'' +
                '}';
    }
}
