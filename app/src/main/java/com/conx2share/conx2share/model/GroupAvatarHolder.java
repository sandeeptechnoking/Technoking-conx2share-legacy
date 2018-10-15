package com.conx2share.conx2share.model;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupAvatarHolder implements Parcelable {

    public static final Parcelable.Creator<GroupAvatarHolder> CREATOR = new Parcelable.Creator<GroupAvatarHolder>() {
        public GroupAvatarHolder createFromParcel(Parcel source) {
            return new GroupAvatarHolder(source);
        }

        public GroupAvatarHolder[] newArray(int size) {
            return new GroupAvatarHolder[size];
        }
    };

    @SerializedName("groupavatar")
    private GroupAvatar groupavatar;

    public GroupAvatarHolder(GroupAvatar groupavatar) {
        this.groupavatar = groupavatar;
    }

    private GroupAvatarHolder(Parcel in) {
        this.groupavatar = in.readParcelable(GroupAvatar.class.getClassLoader());
    }

    public GroupAvatar getGroupAvatar() {
        return groupavatar;
    }

    public void setGroupAvatar(GroupAvatar groupavatar) {
        this.groupavatar = groupavatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupAvatarHolder)) {
            return false;
        }

        GroupAvatarHolder that = (GroupAvatarHolder) o;

        if (groupavatar != null ? !groupavatar.equals(that.groupavatar)
                : that.groupavatar != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return groupavatar != null ? groupavatar.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GroupAvatarHolder{" +
                "groupavatar=" + groupavatar.toString() +
                '}';

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.groupavatar, flags);
    }
}
