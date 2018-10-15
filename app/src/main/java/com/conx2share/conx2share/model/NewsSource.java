package com.conx2share.conx2share.model;


import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsSource implements Parcelable {

    private String name;

    private String url;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("banner_image")
    private String bannerImageUrl;

    public NewsSource(){

    }

    public NewsSource(String name, String url, String imageUrl, String bannerImageUrl) {
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.imageUrl);
        dest.writeString(this.bannerImageUrl);
    }

    private NewsSource(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.imageUrl = in.readString();
        this.bannerImageUrl = in.readString();
    }

    public static final Parcelable.Creator<NewsSource> CREATOR = new Parcelable.Creator<NewsSource>() {
        public NewsSource createFromParcel(Parcel source) {
            return new NewsSource(source);
        }

        public NewsSource[] newArray(int size) {
            return new NewsSource[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NewsSource)) {
            return false;
        }

        NewsSource that = (NewsSource) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null) {
            return false;
        }
        return !(bannerImageUrl != null ? !bannerImageUrl.equals(that.bannerImageUrl)
                : that.bannerImageUrl != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (bannerImageUrl != null ? bannerImageUrl.hashCode() : 0);
        return result;
    }
}
