package com.richfit.data.net.api;


import com.google.gson.JsonObject;
import com.richfit.data.net.http.ProgressRequestBody;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface IRetrofitUploadApi {

    @Multipart
    @POST("https://api.imgur.com/3/image")
    Flowable<ResponseBody> upload(@Part("file\"; filename=\"") RequestBody externalFileParameters);

    @Multipart
    @POST("/upload")
    Flowable<JsonObject> uploadImage(@Part("upload\"; filename=\"1\" ") ProgressRequestBody requestBody);
}
