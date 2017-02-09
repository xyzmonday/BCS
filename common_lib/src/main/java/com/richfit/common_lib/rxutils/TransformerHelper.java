package com.richfit.common_lib.rxutils;


import com.richfit.common_lib.exception.ResponseNullException;
import com.richfit.common_lib.exception.ServerException;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ErrorMessageEntity;
import com.richfit.domain.bean.Response;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by monday on 2016/10/24.
 */

public class TransformerHelper {

    private final static FlowableTransformer TRANSFORMER = upstream ->
            upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

    /**
     * io线程-主线程
     */
    public static <T> FlowableTransformer<T, T> io2main() {
        return (FlowableTransformer<T, T>) TRANSFORMER;
    }

    /**
     * 将服务器返回的Response<T>流装换成T流。
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<Response<T>, T> handleResponse() {
        return flowable ->
                flowable.flatMap(new Function<Response<T>, Publisher<T>>() {
                            @Override
                            public Publisher<T> apply(Response<T> t) throws Exception {
                                if (t == null || (t instanceof List && ((List) t).size() == 0)) {
                                    return Flowable.error(new ResponseNullException("response is null"));
                                } else {
                                    //返回S表示请求成功
                                    if (Global.RETURN_SUCCESS_CODE.equals(t.retCode)) {
                                        return Flowable.just(t.data);
                                    } else {
                                        //返回服务器返回的错误信息
                                        return Flowable.error(new ServerException(t.retCode,t.retMsg));
                                    }
                                }
                            }
                        });
    }


    /**
     * 服务器返回Map<String,Object>的数据格式的转换器
     */
    public final static FlowableTransformer<Map<String,Object>,String> MapTransformer = upstream ->
            upstream.flatMap(map -> {
                final String retCode = (String) map.get("retCode");
                final String retMsg = (String) map.get("retMsg");
                if (Global.RETURN_SUCCESS_CODE.equals(retCode)) {
                    return Flowable.just(retMsg);
                } else {
                    return Flowable.error(new Throwable(retMsg));
                }
            });

    public final static FlowableTransformer<Response<List<ErrorMessageEntity>>,String> ListTransformer = upstream ->
            upstream.flatMap(response->{
                final String retCode = response.retCode;
                final String retMsg = response.retMsg;
                if (Global.RETURN_SUCCESS_CODE.equals(retCode)) {
                    return Flowable.just(retMsg);
                } else {
                    final List<ErrorMessageEntity> datas = response.data;
                    StringBuilder sb= new StringBuilder();
                    for (ErrorMessageEntity data : datas) {
                        sb.append(data.row);
                        sb.append(": ");
                        sb.append(data.message);
                        sb.append(";");
                        sb.append("_");
                    }
                    return Flowable.error(new Throwable(sb.toString()));
                }
            });
}
