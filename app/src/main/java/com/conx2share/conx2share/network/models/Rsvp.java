package com.conx2share.conx2share.network.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Rsvp implements Parcelable {

    private String status;
    private User user;

    public Rsvp(String status, User user) {
        this.status = status;
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeParcelable(this.user, flags);
    }

    protected Rsvp(Parcel in) {
        this.status = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Rsvp> CREATOR = new Creator<Rsvp>() {
        @Override
        public Rsvp createFromParcel(Parcel source) {
            return new Rsvp(source);
        }

        @Override
        public Rsvp[] newArray(int size) {
            return new Rsvp[size];
        }
    };
}
