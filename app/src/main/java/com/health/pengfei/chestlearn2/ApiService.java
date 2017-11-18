package com.health.pengfei.chestlearn2;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by csuml on 8/9/2017.
 */

public interface ApiService {
    @Multipart
    @POST("/function.php")
    Call<ServerResponse> uploadFile(
            @PartMap Map<String, RequestBody> params,
            @Part List<MultipartBody.Part> partList
    );

}