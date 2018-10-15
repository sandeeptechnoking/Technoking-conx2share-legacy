package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    private Image image;

    private String url;

    public Image(String url) {
        this.url = url;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.url);
    }

    protected Image(Parcel in) {
        this.image = in.readParcelable(Image.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Image)) {
            return false;
        }

        Image image1 = (Image) o;

        if (image != null ? !image.equals(image1.image) : image1.image != null) {
            return false;
        }
        if (url != null ? !url.equals(image1.url) : image1.url != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = image != null ? image.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}