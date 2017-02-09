package com.richfit.domain.bean;

/**
 * 下载数据的任务
 */
public class LoadDataTask {
    public int id;
    public String queryType;
    public String queryDate;
    public String queryPage;
    public int pageNum;
    public int pageSize;
    public int startNum;
    public int endNum;
    public boolean isFirstPage;
    public boolean isLoadByPage;

    public LoadDataTask(int id, String queryType, String queryPage, int startNum, int endNum,
                        boolean isLoadByPage, boolean isFirstPage) {
        this.id = id;
        this.queryType = queryType;
        this.queryPage = queryPage;
        this.startNum = startNum;
        this.endNum = endNum;

        this.isLoadByPage = isLoadByPage;
        this.isFirstPage = isFirstPage;
    }

    public LoadDataTask(int id, String queryType, String queryPage, int startNum, int endNum,
                        int pageSize,int pageNum, boolean isLoadByPage, boolean isFirstPage) {
        this.id = id;
        this.queryType = queryType;
        this.queryPage = queryPage;
        this.startNum = startNum;
        this.endNum = endNum;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

        this.isLoadByPage = isLoadByPage;
        this.isFirstPage = isFirstPage;
    }


    @Override
    public String toString() {
        return "LoadDataTask{" +
                "id=" + id +
                ", queryType='" + queryType + '\'' +
                ", queryDate='" + queryDate + '\'' +
                ", queryPage='" + queryPage + '\'' +
                ", startNum=" + startNum +
                ", endNum=" + endNum +
                ", isFirstPage=" + isFirstPage +
                ", isLoadByPage=" + isLoadByPage +
                '}';
    }
}
