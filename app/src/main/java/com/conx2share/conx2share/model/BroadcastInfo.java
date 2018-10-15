package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class BroadcastInfo implements Parcelable {
    private String user_name;

    private String password;

    private String url;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user_name);
        dest.writeString(this.password);
        dest.writeString(this.url);
    }

    public BroadcastInfo() {
    }

    protected BroadcastInfo(Parcel in) {
        this.user_name = in.readString();
        this.password = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<BroadcastInfo> CREATOR = new Parcelable.Creator<BroadcastInfo>() {
        @Override
        public BroadcastInfo createFromParcel(Parcel source) {
            return new BroadcastInfo(source);
        }

        @Override
        public BroadcastInfo[] newArray(int size) {
            return new BroadcastInfo[size];
        }
    };

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}