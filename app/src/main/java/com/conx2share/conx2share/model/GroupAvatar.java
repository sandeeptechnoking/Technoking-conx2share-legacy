package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupAvatar implements Parcelable {

    public static final Parcelable.Creator<GroupAvatar> CREATOR = new Parcelable.Creator<GroupAvatar>() {
        public GroupAvatar createFromParcel(Parcel source) {
            return new GroupAvatar(source);
        }

        public GroupAvatar[] newArray(int size) {
            return new GroupAvatar[size];
        }
    };

    private String url;

    public GroupAvatar(String url) {
        this.url = url;
    }

    private GroupAvatar(Parcel in) {
        this.url = in.readString();
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
        dest.writeString(this.url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupAvatar)) {
            return false;
        }

        GroupAvatar that = (GroupAvatar) o;

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {

        return "GroupAvatar{" +
                "url='" + url + '\'' +
                '}';
    }
}
