package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Like implements Parcelable {

    private Liker liker;
    private String id;
    private String updated_at;
    private String created_at;
    private String likee_id;
    private String liker_id;

    public Liker getLiker() {
        return liker;
    }

    public void setLiker(Liker liker) {
        this.liker = liker;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLikee_id() {
        return likee_id;
    }

    public void setLikee_id(String likee_id) {
        this.likee_id = likee_id;
    }

    public String getLiker_id() {
        return liker_id;
    }

    public void setLiker_id(String liker_id) {
        this.liker_id = liker_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.liker, flags);
        dest.writeString(this.id);
        dest.writeString(this.updated_at);
        dest.writeString(this.created_at);
        dest.writeString(this.likee_id);
        dest.writeString(this.liker_id);
    }

    public Like() {
    }

    protected Like(Parcel in) {
        this.liker = in.readParcelable(Liker.class.getClassLoader());
        this.id = in.readString();
        this.updated_at = in.readString();
        this.created_at = in.readString();
        this.likee_id = in.readString();
        this.liker_id = in.readString();
    }

    public static final Parcelable.Creator<Like> CREATOR = new Parcelable.Creator<Like>() {
        @Override
        public Like createFromParcel(Parcel source) {
            return new Like(source);
        }

        @Override
        public Like[] newArray(int size) {
            return new Like[size];
        }
    };
}