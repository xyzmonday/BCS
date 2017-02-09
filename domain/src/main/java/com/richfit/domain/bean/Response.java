package com.richfit.domain.bean;

/**
 * 服务返回的json数据，假设为一下形式：
 * {"returnCode": "S", "returnMsg": "success"}
 * {"returnCode": "S", "returnMsg": "success", "data":{...}}
 * {"returnCode": "S", "returnMsg": "success", "data":[{...}, {...}], "currentPage": 1, "pageSize": 20, "maxCount": 2, "maxPage": 1}
 * 所以这样封装的实体类如下
 *
 * @param <T>
 */
public class Response<T> {
    public String retCode;
    public String retMsg;
    public T data;
}
