package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class LiveStream implements Parcelable {

    private String android;
    private String ios;

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getIos() {
        return ios;
    }

    public void setIos(String ios) {
        this.ios = ios;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.android);
        dest.writeString(this.ios);
    }

    public LiveStream() {
    }

    private LiveStream(Parcel in) {
        this.android = in.readString();
        this.ios = in.readString();
    }

    public static final Parcelable.Creator<LiveStream> CREATOR = new Parcelable.Creator<LiveStream>() {
        @Override
        public LiveStream createFromParcel(Parcel source) {
            return new LiveStream(source);
        }

        @Override
        public LiveStream[] newArray(int size) {
            return new LiveStream[size];
        }
    };

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}