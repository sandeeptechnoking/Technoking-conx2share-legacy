package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;


public class UserCover implements Parcelable {

    private Picture coverPhoto;

    public Picture getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(Picture coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.coverPhoto, flags);
    }

    protected UserCover(Parcel in) {
        this.coverPhoto = in.readParcelable(Picture.class.getClassLoader());
    }

    public static final Creator<UserCover> CREATOR = new Creator<UserCover>() {
        @Override
        public UserCover createFromParcel(Parcel source) {
            return new UserCover(source);
        }

        @Override
        public UserCover[] newArray(int size) {
            return new UserCover[size];
        }
    };
}
