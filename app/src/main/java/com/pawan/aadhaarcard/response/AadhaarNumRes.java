package com.pawan.aadhaarcard.response;

import com.google.gson.annotations.SerializedName;

public class AadhaarNumRes {
    @SerializedName("request_id")
    private String requestId;

    public AadhaarNumRes(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
