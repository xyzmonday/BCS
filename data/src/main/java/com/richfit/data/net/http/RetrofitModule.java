package com.richfit.data.net.http;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.NetworkStateUtil;
import com.richfit.data.net.api.IRequestApi;
import com.richfit.data.net.api.IRetrofitDownloadApi;
import com.richfit.data.net.api.IRetrofitUploadApi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitModule {

    private static IRequestApi request;

    /**
     * 缓存配置说明：
     * 1）对于request, 无网时, 使用CacheControl.FORCE_CACHE, 即使用缓存.
     * 2）对于request,网络时，使用Cache-Control。"Cache-Control"请求头就是用来控制缓存的.
     * 它有一些属性值来指定缓存的属性, 诸如公共属性, 是否可缓存以及缓存的有效期等.
     * 3）对于response, 无网时, 使用CacheControl.FORCE_CACHE(这种情况较为少见).
     * 4）对于response, 有网时, 加入和request一样的Cache-Control;
     *
     * @return
     */
    public static IRequestApi getRequestApiwithCacheConfig(Context context) {

        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //打印拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> L.d(message));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RewriteCacheControllerInterceptor(context.getApplicationContext()))
                .addInterceptor(logging)//添加打印拦截器
                .connectTimeout(15, TimeUnit.SECONDS)//设置请求超时时间
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .cache(cache).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(IRequestApi.class);
    }

    /**
     * 缓存拦截器
     */
    private static class RewriteCacheControllerInterceptor implements Interceptor {

        Context mContext;

        public RewriteCacheControllerInterceptor(Context context) {
            this.mContext = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();
            Request request1 = chain.request();
            //如果没有网络，缓存request
            if (!NetworkStateUtil.isNetConnected(mContext)) {
                request1 = request1.newBuilder()
                        .cacheControl(cacheControl)
                        .build();

            }

            Response originalResponse = chain.proceed(request1);
            //如果有网络，直接从网络获取response
            if (NetworkStateUtil.isNetConnected(mContext)) {
                int maxAge = 0 * 60; //设置缓存时间为0小时
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                //如果没有网络从缓存中获取
                int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }


    /**
     * 一般网络请求 get/post/...
     */
    public static IRequestApi getRequestApi(Context context) {
        if (request == null) {
            //拦截器
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request oldRequest = chain.request();
                    // 添加新的参数
                    HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                            .newBuilder()
                            .scheme(oldRequest.url().scheme())
                            .host(oldRequest.url().host())
                            .addQueryParameter("macAddress", Global.macAddress)
                            .addQueryParameter("serialNum", Global.serialNum)
                            .addQueryParameter("loginId",Global.LOGIN_ID)
                            .addQueryParameter("userId",Global.USER_ID);


                    // 新的请求
                    Request newRequest = oldRequest.newBuilder()
                            .method(oldRequest.method(), oldRequest.body())
                            .header("registeredChannels", "2")//来自1：iOS,2:Android,3:web
                            .url(authorizedUrlBuilder.build())
                            .build();

                    return chain.proceed(newRequest);
                }
            };
            //打印拦截器
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> L.i(message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)//添加拦截器
                    .addInterceptor(logging)//添加打印拦截器
                    .connectTimeout(10, TimeUnit.SECONDS)//设置请求超时时间
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Global.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();
            request = retrofit.create(IRequestApi.class);
        }
        return request;
    }

    /**
     * 下载文件
     */
    public static IRetrofitDownloadApi getDownloadApi() {
        //打印拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body()))
                                .build();
                    }
                })
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)//设置请求超时时间
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build();
        return retrofit.create(IRetrofitDownloadApi.class);
    }

    /**
     * 上传文件/图片
     */
    public static IRetrofitUploadApi getUploadApi() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)//设置请求超时时间
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build();
        return retrofit.create(IRetrofitUploadApi.class);
    }
}

