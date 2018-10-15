package com.conx2share.conx2share.model;


import com.google.gson.annotations.SerializedName;

public class RegisteredDeviceResponse {

    public int status;
    public String message;
    public Device device;

    public class Device {

        public String os;
        @SerializedName("user_id")
        public int userId;
        @SerializedName("id")
        public int deviceId;
        @SerializedName("uid")
        public String uId;

    }
}
