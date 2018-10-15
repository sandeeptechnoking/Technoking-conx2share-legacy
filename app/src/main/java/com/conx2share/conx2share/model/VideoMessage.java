package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoMessage implements Parcelable {

    public static final Parcelable.Creator<VideoMessage> CREATOR = new Parcelable.Creator<VideoMessage>() {
        public VideoMessage createFromParcel(Parcel source) {
            return new VideoMessage(source);
        }

        public VideoMessage[] newArray(int size) {
            return new VideoMessage[size];
        }
    };

    private Video video;

    public VideoMessage(Video video) {
        this.video = video;
    }

    private VideoMessage(Parcel in) {
        this.video = in.readParcelable(Video.class.getClassLoader());
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
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
        if (!(o instanceof VideoMessage)) {
            return false;
        }

        VideoMessage that = (VideoMessage) o;

        if (video != null ? !video.equals(that.video) : that.video != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return video != null ? video.hashCode() : 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.video, 0);
    }
}
