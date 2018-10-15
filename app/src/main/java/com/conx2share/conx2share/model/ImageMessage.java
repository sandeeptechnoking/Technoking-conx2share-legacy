package com.conx2share.conx2share.model;


import android.os.Parcel;
import android.os.Parcelable;

public class ImageMessage implements Parcelable {

    public static final Parcelable.Creator<ImageMessage> CREATOR = new Parcelable.Creator<ImageMessage>() {
        public ImageMessage createFromParcel(Parcel source) {
            return new ImageMessage(source);
        }

        public ImageMessage[] newArray(int size) {
            return new ImageMessage[size];
        }
    };

    private Picture image;

    public ImageMessage(Picture image) {
        this.image = image;
    }

    private ImageMessage(Parcel in) {
        this.image = in.readParcelable(Picture.class.getClassLoader());
    }

    public Picture getImage() {
        return image;
    }

    public void setImage(Picture image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.image, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImageMessage)) {
            return false;
        }

        ImageMessage that = (ImageMessage) o;

        if (image != null ? !image.equals(that.image) : that.image != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return image != null ? image.hashCode() : 0;
    }
}
