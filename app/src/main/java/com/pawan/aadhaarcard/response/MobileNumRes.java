package com.pawan.aadhaarcard.response;

import com.google.gson.annotations.SerializedName;

public class MobileNumRes {
    @SerializedName("result")
    private Result result;

    public String getMobileNumber () {
        return (result != null && result.getSourceOutput() != null) ? result.getSourceOutput().getMobileNumber() : null;
    }

    public static class Result {
        @SerializedName("source_output")
        private SourceOutput sourceOutput;

        public SourceOutput getSourceOutput() {
            return sourceOutput;
        }
    }

    public static class SourceOutput {
        @SerializedName("mobile_number")
        private String mobileNumber;

        public String getMobileNumber() {
            return mobileNumber;
        }
    }
}
