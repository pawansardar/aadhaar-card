package com.pawan.aadhaarcard.api_service;

import com.pawan.aadhaarcard.request.AadhaarNumReq;
import com.pawan.aadhaarcard.response.AadhaarNumRes;
import com.pawan.aadhaarcard.response.MobileNumRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AadhaarVerificationApiService {
    @POST("tasks/async/verify_with_source/aadhaar_lite")
    Call<AadhaarNumRes> aadhaar_lite(@Body AadhaarNumReq aadhaarNumReq);

    @GET("tasks")
    Call<List<MobileNumRes>> requestMob(@Query("request_id") String request_id);
}
