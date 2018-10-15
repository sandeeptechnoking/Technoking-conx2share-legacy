package com.conx2share.conx2share.model;

public class DeviceAttributes {

    private String os;
    private String uid;
    private String app_version;

    public DeviceAttributes(String os, String uid) {
        this.os = os;
        this.uid = uid;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }
}
