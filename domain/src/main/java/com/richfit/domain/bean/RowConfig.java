package com.richfit.domain.bean;

/**
 * 每一行的动态配置
 * Created by monday on 2016/11/8.
 */

public class RowConfig{

    /*该配置文件行对应的id*/
    public String id;
    /*控件中文名称，该属性一般用于在抬头和数据采集界面，作为左边的TextView的文本*/
    public String propertyName;
    /*控件英文名称，该属性作为该控件绑定的数据对应的key*/
    public String propertyCode;
    /*显示标志*/
    public String displayFlag;
    /*该控件是否必输*/
    public String inputFlag;
    /*地区公司的编码*/
    public String companyId;
    public String businessType;
    /*单据编码和类型*/
    public String refType;
    /*配置文件的类型，0表示抬头，1表示明细父节点，2表示明细子节点,3表示数据采集*/
    public String configType;
    /*控件类型TextView(0);EditText(1),RichEditText(2);Spinner(3);
    Checkbox(4);RadioBox(5);Button(6);AutoEdit（7）*/
    public String uiType;
    /*回传map标志，1表示抬头，2，表示行，3表示仓位*/
    public String colNum;
    /*回传map的key*/
    public String colName;
    /*额外数据字典表*/
    public String dataSource;

    @Override
    public String toString() {
        return "RowConfig{" +
                "id='" + id + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", propertyCode='" + propertyCode + '\'' +
                ", displayFlag='" + displayFlag + '\'' +
                ", inputFlag='" + inputFlag + '\'' +
                ", companyId='" + companyId + '\'' +
                ", businessType='" + businessType + '\'' +
                ", refType='" + refType + '\'' +
                ", configType='" + configType + '\'' +
                ", uiType='" + uiType + '\'' +
                ", colNum='" + colNum + '\'' +
                ", colName='" + colName + '\'' +
                ", dataSource='" + dataSource + '\'' +
                '}';
    }
}
