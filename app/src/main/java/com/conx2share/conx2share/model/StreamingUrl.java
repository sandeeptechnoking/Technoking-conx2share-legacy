package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StreamingUrl implements Parcelable {

    public static final Parcelable.Creator<StreamingUrl> CREATOR = new Parcelable.Creator<StreamingUrl>() {

        public StreamingUrl createFromParcel(Parcel source) {
            return new StreamingUrl(source);
        }

        public StreamingUrl[] newArray(int size) {
            return new StreamingUrl[size];
        }
    };

    private String android;

    private String ios;

    private String web;

    public StreamingUrl() {

    }

    public StreamingUrl(String android, String ios, String web) {
        this.android = android;
        this.ios = ios;
        this.web = web;
    }

    public StreamingUrl(String android) {
        this.android = android;
    }

    private StreamingUrl(Parcel in) {
        this.android = in.readString();
        this.ios = in.readString();
        this.web = in.readString();
    }

    @Override
    public String toString() {
        return "StreamingUrl{" +
                "android='" + android + '\'' +
                ", ios='" + ios + '\'' +
                ", web='" + web + '\'' +
                '}';
    }

    public String getWeb() {

        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.android);
        dest.writeString(this.ios);
        dest.writeString(this.web);
    }

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StreamingUrl that = (StreamingUrl) o;

        if (android != null ? !android.equals(that.android) : that.android != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return android != null ? android.hashCode() : 0;
    }
}
