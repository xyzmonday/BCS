package com.richfit.domain.bean;

/**
 * Created by monday on 2016/3/14.
 */
public class UpdateEntity {
    public String appVersion;
    public String appUpdateDesc;
    public String appDownloadUrl;
    public String appName;

    @Override
    public String toString() {
        return "UpdateEntity{" +
                "appVersion='" + appVersion + '\'' +
                ", appUpdateDesc='" + appUpdateDesc + '\'' +
                ", appDownloadUrl='" + appDownloadUrl + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}
