package com.richfit.domain.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 子功能模块的描述
 * Created by monday on 2016/11/9.
 */

public class SubFuncEntity implements Parcelable {

    public SubFuncEntity() {
    }

    public String id;
    public String parentId;
    public String bizType;
    public String refType;
    /**
     * 该子功能所属的地区公司
     */
    public String companyCode;
    public String companyName;
    /**
     * 所属的模块名称和编码
     */
    public String modularCode;
    public String modularName;
    /**
     * 子功能名称和编码
     */
    public String subFunCode;
    public String subFunName;
    /**
     * 子功能的icon
     */
    public int subFunIcon;
    /**
     * 操作子功能的日期
     */
    public String lastOperDate;
    /**
     * 该子功能的未上传的业务数量
     */
    public int unFinishedNum;

    /**
     * 该子功能是否被选中
     */
    public boolean isChecked;

    /**
     * 子功能的不同额外配置文件(包括了抬头，明细，以及数据采集)
     */
    public ArrayList<RowConfig> headerConfigs;
    public ArrayList<RowConfig> parentNodeConfigs;
    public ArrayList<RowConfig> childNodeConfigs;
    public ArrayList<RowConfig> collectionConfigs;
    public ArrayList<RowConfig> locationConfigs;

    protected SubFuncEntity(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        bizType = in.readString();
        refType = in.readString();
        companyCode = in.readString();
        companyName = in.readString();
        modularCode = in.readString();
        modularName = in.readString();
        subFunCode = in.readString();
        subFunName = in.readString();
        subFunIcon = in.readInt();
        lastOperDate = in.readString();
        unFinishedNum = in.readInt();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(parentId);
        dest.writeString(bizType);
        dest.writeString(refType);
        dest.writeString(companyCode);
        dest.writeString(companyName);
        dest.writeString(modularCode);
        dest.writeString(modularName);
        dest.writeString(subFunCode);
        dest.writeString(subFunName);
        dest.writeInt(subFunIcon);
        dest.writeString(lastOperDate);
        dest.writeInt(unFinishedNum);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubFuncEntity> CREATOR = new Creator<SubFuncEntity>() {
        @Override
        public SubFuncEntity createFromParcel(Parcel in) {
            return new SubFuncEntity(in);
        }

        @Override
        public SubFuncEntity[] newArray(int size) {
            return new SubFuncEntity[size];
        }
    };
}
