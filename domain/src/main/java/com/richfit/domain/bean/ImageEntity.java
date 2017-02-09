package com.richfit.domain.bean;

/**
 * 图片实体类
 * Created by monday on 2016/1/26.
 */
public class ImageEntity {
    public boolean isSelected;
    public String imageDir;//存储目录
    public String imageName;//图片名称
    public String createBy;
    public String bizType;
    public String refType;
    public String createDate;
    public Long lastModifiedTime;//创建时间
    /*图片类型，有合格证，技术附件，质量报告，其他*/
    public int takePhotoType;
    //单据行id
    public String refLineId;
    public String id;

    @Override
    public String toString() {
        return "ImageEntity{" +
                "isSelected=" + isSelected +
                ", imageDir='" + imageDir + '\'' +
                ", imageName='" + imageName + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate='" + createDate + '\'' +
                ", lastModifiedTime=" + lastModifiedTime +
                ", takePhotoType='" + takePhotoType + '\'' +
                ", refLineId='" + refLineId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
