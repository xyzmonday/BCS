package com.richfit.domain.bean;

/**
 * 分页下载基础数据的包装类
 * Created by monday on 2016/11/14.
 */

public class LoadBasicDataWrapper {
    /*基础数据下载类型，包括组织机构，供应商，仓位等*/
    public String queryType;
    /*该类基础数据的总条数*/
    public int totalCount;
    /*是否需要分页下载*/
    public boolean isByPage;
    /*分页加载时，指定需要加载第几页*/
    public int pageNum;
    /*指定该页加载多少条数据*/
    public int pageSize;


}
