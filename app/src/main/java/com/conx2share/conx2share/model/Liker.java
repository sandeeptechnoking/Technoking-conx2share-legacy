package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.conx2share.conx2share.network.models.User;

public class Liker implements Parcelable {

    private User user;

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
        dest.writeParcelable(this.user, flags);
    }

    public Liker() {
    }

    protected Liker(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Liker> CREATOR = new Parcelable.Creator<Liker>() {
        @Override
        public Liker createFromParcel(Parcel source) {
            return new Liker(source);
        }

        @Override
        public Liker[] newArray(int size) {
            return new Liker[size];
        }
    };
}