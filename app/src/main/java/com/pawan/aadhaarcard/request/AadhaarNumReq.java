package com.pawan.aadhaarcard.request;

import com.google.gson.annotations.SerializedName;

public class AadhaarNumReq {
    @SerializedName("task_id")
    private String taskId;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("data")
    private Data data;

    public AadhaarNumReq(String taskId, String groupId, String aadhaarNumber) {
        this.taskId = taskId;
        this.groupId = groupId;
        this.data = new Data(aadhaarNumber);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("aadhaar_number")
        private String aadhaarNumber;

        public Data(String aadhaarNumber) {
            this.aadhaarNumber = aadhaarNumber;
        }

        public String getAadhaarNumber() {
            return aadhaarNumber;
        }

        public void setAadhaarNumber(String aadhaarNumber) {
            this.aadhaarNumber = aadhaarNumber;
        }
    }
}
