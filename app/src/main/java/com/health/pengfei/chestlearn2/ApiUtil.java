package com.health.pengfei.chestlearn2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by csuml on 8/9/2017.
 */

public class ApiUtil {
    private static final String HOST = "http://erx.southcentralus.cloudapp.azure.com";
    private static Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 10;

    public static String getHost(){
        return HOST;
    }

    private static Retrofit getApiRetrofit(){
        if (retrofit == null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .client(okHttpBuilder.build())
                    .baseUrl(HOST)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

        }
        return retrofit;
    }

    private static ApiService getApiService(){
        return  ApiUtil.getApiRetrofit().create(ApiService.class);
    }

    public static Call<ServerResponse> uploadFile(List<MultipartBody.Part> partList, Map<String, RequestBody> params){
        return ApiUtil.getApiService().uploadFile(params, partList);
    }
}