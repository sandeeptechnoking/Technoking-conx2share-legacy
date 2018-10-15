package com.conx2share.conx2share.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserTag implements Parcelable {

    private Integer id;

    private int userId;

    private int taggerId;

    private Integer taggedObjectId;

    private String taggedObjectType;

    private Date createdAt;

    private String tag;

    public static final Creator<UserTag> CREATOR = new Creator<UserTag>() {
        @Override
        public UserTag createFromParcel(Parcel source) {
            return new UserTag(source);
        }

        @Override
        public UserTag[] newArray(int size) {
            return new UserTag[0];
        }
    };

    public UserTag() {
        // NO OP
    }

    public UserTag(Parcel source) {
        id = source.readInt();
        userId = source.readInt();
        taggerId = source.readInt();
        taggedObjectId = source.readInt();
        taggedObjectType = source.readString();
        tag = source.readString();
        createdAt = (Date) source.readValue(Date.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(taggerId);
        dest.writeInt(taggedObjectId);
        dest.writeString(taggedObjectType);
        dest.writeString(tag);
        dest.writeValue(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(int taggerId) {
        this.taggerId = taggerId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaggedObjectId() {
        return taggedObjectId;
    }

    public void setTaggedObjectId(int taggedObjectId) {
        this.taggedObjectId = taggedObjectId;
    }

    public String getTaggedObjectType() {
        return taggedObjectType;
    }

    public void setTaggedObjectType(String taggedObjectType) {
        this.taggedObjectType = taggedObjectType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
