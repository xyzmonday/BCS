package com.richfit.data.net.api;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface IRetrofitDownloadApi {

    @Streaming
    @GET("{fileName}")
    Flowable<ResponseBody> downLoadApk(@Path("fileName") String fileName);

}
