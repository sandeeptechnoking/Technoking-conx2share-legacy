package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    private String url;

    private Video video;

    public Video() {

    }

    public Video(String url) {
        this.url = url;
    }

    public Video(String url, Video video) {
        this.url = url;
        this.video = video;
    }

    private Video(Parcel in) {
        this.url = in.readString();
        this.video = in.readParcelable(Video.class.getClassLoader());
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
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
        if (!(o instanceof Video)) {
            return false;
        }

        Video video1 = (Video) o;

        if (url != null ? !url.equals(video1.url) : video1.url != null) {
            return false;
        }
        if (video != null ? !video.equals(video1.video) : video1.video != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (video != null ? video.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.video, flags);
    }

    @Override
    public String toString() {
        return "Video{" +
                "url='" + url + '\'' +
                ", video=" + video +
                '}';
    }
}
