package com.richfit.domain.bean;

/**
 * 页面的配置
 * Created by monday on 2017/1/10.
 */

public class BizFragmentConfig {
    public String id;
    public String fragmentTag;
    public String bizType;
    public String refType;
    public String tabTitle;
    public int fragmentType;
    public String className;

    @Override
    public String toString() {
        return "BizFragmentConfig{" +
                "id='" + id + '\'' +
                ", fragmentTag='" + fragmentTag + '\'' +
                ", bizType='" + bizType + '\'' +
                ", refType='" + refType + '\'' +
                ", tabTitle='" + tabTitle + '\'' +
                ", fragmentType=" + fragmentType +
                ", className='" + className + '\'' +
                '}';
    }
}
