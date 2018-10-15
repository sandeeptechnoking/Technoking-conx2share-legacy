package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.conx2share.conx2share.network.models.User;

import java.util.ArrayList;

public class Friend implements Parcelable {

    public static final int STAR_FRIEND_OBJECT_ID = -1;

    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    private int friendId;

    private Integer friendMessageCount;

    private String friendFirstName;

    private String friendLastName;

    private String friendPhotoUrl;

    private ArrayList<Message> messages;

    private String handle;

    public Friend(User user){
        this.friendId = user.getId();
        this.friendFirstName = user.getFirstName();
        this.friendLastName = user.getLastName();
        this.friendPhotoUrl = user.getAvatarUrl();
        this.messages = new ArrayList<>();
        this.handle = user.getUsername();
    }

    public Friend(int friendId, Integer friendMessageCount, String friendFirstName, String friendLastName, String friendPhotoUrl, ArrayList<Message> messages, String handle) {
        this.friendId = friendId;
        this.friendMessageCount = friendMessageCount;
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.friendPhotoUrl = friendPhotoUrl;
        this.messages = messages;
        this.handle = handle;
    }

    private Friend(Parcel in) {
        this.friendId = in.readInt();
        this.friendMessageCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.friendFirstName = in.readString();
        this.friendLastName = in.readString();
        this.friendPhotoUrl = in.readString();
        this.messages = new ArrayList<>();
        in.readTypedList(this.messages, Message.CREATOR);
        handle = in.readString();
    }

    public static Friend getStarFriendObject(){
        return new Friend(STAR_FRIEND_OBJECT_ID, 0, "", "", "" ,new ArrayList<>(), "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.friendId);
        dest.writeValue(this.friendMessageCount);
        dest.writeString(this.friendFirstName);
        dest.writeString(this.friendLastName);
        dest.writeString(this.friendPhotoUrl);
        dest.writeTypedList(this.messages);
        dest.writeString(handle);
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public Integer getFriendMessageCount() {
        return friendMessageCount;
    }

    public void setFriendMessageCount(Integer friendMessageCount) {
        this.friendMessageCount = friendMessageCount;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public void setFriendFirstName(String friendFirstName) {
        this.friendFirstName = friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public void setFriendLastName(String friendLastName) {
        this.friendLastName = friendLastName;
    }

    public String getFriendPhotoUrl() {
        return friendPhotoUrl;
    }

    public void setFriendPhotoUrl(String friendPhotoUrl) {
        this.friendPhotoUrl = friendPhotoUrl;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
