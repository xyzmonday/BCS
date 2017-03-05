package com.richfit.data.net.api;


import com.richfit.domain.bean.ErrorMessageEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.MenuNode;
import com.richfit.domain.bean.RefNumEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.Response;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.UpdateEntity;
import com.richfit.domain.bean.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Created by wukewei on 16/5/26.
 */
public interface IRequestApi {

    @GET("syncDate")
    Flowable<Map<String,Object>> syncDate();

    @GET("getMappingInfo")
    Flowable<Map<String,Object>> getMappingInfo();

    @GET("getLoginInfo")
    Flowable<Response<UserEntity>> login(@Query("requestParam") String requestParam);

    @GET("getMenuTreeInfo")
    Flowable<Response<ArrayList<MenuNode>>> getMenuTreeInfo(@Query("requestParam") String requestParam);

    @GET("getConfigInfo")
    Flowable<Response<ArrayList<RowConfig>>> loadExtraConfig(@Query("requestParam") String requestParam);

    @GET("getReferenceInfo")
    Flowable<Response<ReferenceEntity>> getReference(@Query("requestParam") String requestParam);

    @GET("getIncrementalInfo")
    Flowable<Map<String,Object>> preparePageLoad(@Query("requestParam") String requestParam);

    @GET("getIncrementalInfo")
    Flowable<Response<List<Map<String, Object>>>> loadBasicData(@Query("requestParam") String requestParam);

    @GET("getTransferInfo")
    Flowable<Response<ReferenceEntity>> getCacheTransfereInfo(@Query("requestParam") String requestParam);

    /*数据采集界面有参考获取缓存*/
    @GET("getTransferInfoSingle")
    Flowable<Response<ReferenceEntity>> getTransferInfoSingle(@Query("requestParam") String requestParam);

    @GET("uploadCollectionDataSingle")
    Flowable<Map<String, Object>> uploadCollectionDataSingle(@Query("requestParam") String requestParam);

    @GET("deleteCollectionDataSingle")
    Flowable<Map<String, Object>> deleteCollectionDataSingle(@Query("requestParam") String requestParam);

    @GET("uploadCollectionData")
    Flowable<Map<String, Object>> uploadCollectionData(@Query("requestParam") String requestParam);

    @GET("transferCollectionData")
    Flowable<Response<List<ErrorMessageEntity>>> transferCollectionData(@Query("requestParam") String requestParam);

    @GET("transferCollectionData")
    Flowable<Response<List<ErrorMessageEntity>>> transferCollectionData2(@Query("requestParam") String requestParam);

    @GET("getReferenceInfo")
    Flowable<Response<List<RefNumEntity>>> getReserveNumList(@Query("requestParam") String requestParam);

    @GET("deleteCollectionData")
    Flowable<Map<String, Object>> deleteCollectionData(@Query("requestParam") String requestParam);

    @Multipart
    @POST("uploadInspectionImage")
    Flowable<Map<String, Object>> uploadInspectionImage(@Part() MultipartBody.Part photo, @Part("requestParam") RequestBody requestParam);

    /*获取盘点头信息*/
    @GET("getCheckInfo")
    Flowable<Response<ReferenceEntity>> getCheckInfo(@Query("requestParam") String requestParam);

    @GET("deleteCheckData")
    Flowable<Map<String, Object>> deleteCheckData(@Query("requestParam") String requestParam);

    @GET("getCheckTransferInfoSingle")
    Flowable<Response<List<InventoryEntity>>> getCheckTransferInfoSingle(@Query("requestParam") String requestParam);

    @GET("uploadCheckDataSingle")
    Flowable<Map<String, Object>> uploadCheckDataSingle(@Query("requestParam") String requestParam);

    @GET("getCheckTransferInfo")
    Flowable<Response<ReferenceEntity>> getCheckTransferInfo(@Query("requestParam") String requestParam);

    @GET("deleteCheckDataSingle")
    Flowable<Map<String, Object>> deleteCheckDataSingle(@Query("requestParam") String requestParam);

    @GET("getInventoryInfo")
    Flowable<Response<List<InventoryEntity>>> getInventoryInfo(@Query("requestParam") String requestParam);

    @GET("getLocationInfo")
    Flowable<Map<String, Object>> getLocation(@Query("requestParam") String requestParam);

    @GET("getAppVersion")
    Flowable<Response<UpdateEntity>> getAppVersion();

    @GET("changeLoginInfo")
    Flowable<Map<String,Object>> changeLoginInfo(@Query("requestParam") String requestParam);

    @GET("uploadInspectionDataOffline")
    Flowable<Map<String,Object>> uploadInspectionDataOffline(@Query("requestParam") String requestParam);

    @GET("getMaterialInfo")
    Flowable<Response<MaterialEntity>> getMaterialInfo(@Query("requestParam") String requestParam);

    @GET("transferCheckData")
    Flowable<Map<String,Object>> transferCheckData(@Query("requestParam") String requestParam);
}
