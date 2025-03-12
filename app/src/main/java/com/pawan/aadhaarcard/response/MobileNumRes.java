package com.pawan.aadhaarcard.response;

import com.google.gson.annotations.SerializedName;

public class MobileNumRes {
    @SerializedName("result")
    private Result result;

    @SerializedName("status")
    private String status;

    public String getMobileNumber () {
        return (result != null && result.getSourceOutput() != null) ? result.getSourceOutput().getMobileNumber() : null;
    }

    public String getState() {
        return (result != null && result.getSourceOutput() != null) ? result.getSourceOutput().getState() : null;
    }

    public String getGender() {
        return (result != null && result.getSourceOutput() != null) ? result.getSourceOutput().getGender() : null;
    }

    public String getStatus() {
        return status;
    }

    public static class Result {
        @SerializedName("source_output")
        private SourceOutput sourceOutput;

        public SourceOutput getSourceOutput() {
            return sourceOutput;
        }
    }

    public static class SourceOutput {
        @SerializedName("gender")
        private String gender;
        @SerializedName("mobile_number")
        private String mobileNumber;

        @SerializedName("state")
        private String state;

        public String getGender() {
            return gender;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getState() {
            return state;
        }
    }
}
