package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAvatar implements Parcelable {

    private Picture avatar;

    private String url;

    public UserAvatar(Picture avatar) {
        this.avatar = avatar;
    }

    public Picture getAvatar() {
        return avatar;
    }

    public void setAvatar(Picture avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserAvatar{" +
                "avatar=" + avatar +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAvatar)) {
            return false;
        }

        UserAvatar that = (UserAvatar) o;

        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = avatar != null ? avatar.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.avatar, flags);
        dest.writeString(this.url);
    }

    protected UserAvatar(Parcel in) {
        this.avatar = in.readParcelable(Picture.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Creator<UserAvatar> CREATOR = new Creator<UserAvatar>() {
        @Override
        public UserAvatar createFromParcel(Parcel source) {
            return new UserAvatar(source);
        }

        @Override
        public UserAvatar[] newArray(int size) {
            return new UserAvatar[size];
        }
    };
}
