package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PostPicture implements Parcelable {

    public static final Parcelable.Creator<PostPicture> CREATOR = new Parcelable.Creator<PostPicture>() {

        public PostPicture createFromParcel(Parcel source) {
            return new PostPicture(source);
        }

        public PostPicture[] newArray(int size) {
            return new PostPicture[size];
        }
    };

    private Picture picture;

    public PostPicture(Picture picture) {
        this.picture = picture;
    }

    private PostPicture(Parcel in) {

        this.picture = in.readParcelable(Picture.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostPicture)) {
            return false;
        }

        PostPicture that = (PostPicture) o;

        if (picture != null ? !picture.equals(that.picture) : that.picture != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return picture != null ? picture.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PostPicture{" +
                "picture=" + picture +

                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(this.picture, flags);
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
