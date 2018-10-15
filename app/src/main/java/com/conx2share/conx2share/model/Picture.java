package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {

    private String url;
    private NestedUrl feed;
    private NestedUrl thumb;
    private int width;
    private int height;

    public Picture() {
    }

    public Picture(String url) {
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public String getFeedUrl() {
        if (feed != null) {
            return feed.url;
        } else {
            return "";
        }
    }

    public String getThumbUrl() {
        if (thumb != null) {
            return thumb.url;
        } else if (url != null) {
            return url;
        }
        return "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Picture)) {
            return false;
        }

        Picture picture = (Picture) o;

        if (url != null ? !url.equals(picture.url) : picture.url != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    static class NestedUrl implements Parcelable {
        public static final Creator<NestedUrl> CREATOR = new Creator<NestedUrl>() {
            @Override
            public NestedUrl createFromParcel(Parcel in) {
                return new NestedUrl(in);
            }

            @Override
            public NestedUrl[] newArray(int size) {
                return new NestedUrl[size];
            }
        };

        String url;

        protected NestedUrl(Parcel in) {
            url = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.feed, flags);
        dest.writeParcelable(this.thumb, flags);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected Picture(Parcel in) {
        this.url = in.readString();
        this.feed = in.readParcelable(NestedUrl.class.getClassLoader());
        this.thumb = in.readParcelable(NestedUrl.class.getClassLoader());
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
}