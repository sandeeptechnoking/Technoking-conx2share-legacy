package com.conx2share.conx2share.model;

import com.conx2share.conx2share.ui.feed.post.PostReceiver;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Business implements Parcelable, PostReceiver {

    public static final String EXTRA = "extra_business";

    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>() {
        public Business createFromParcel(Parcel source) {
            return new Business(source);
        }

        public Business[] newArray(int size) {
            return new Business[size];
        }
    };

    private Integer id;

    private String name;

    private String about;

    private UserAvatar avatar;

    private Boolean isOwner;

    private Boolean isFollowing;

    private Integer badgeCount;

    private String store_url;

    public Business() {
        // NO OP
    }

    public Business(Integer id, String name, String about, UserAvatar avatar, Boolean isOwner) {
        this.id = id;
        this.name = name;
        this.about = about;
        this.avatar = avatar;
        this.isOwner = isOwner;
        this.isFollowing = false;
    }

    public Business(Parcel source) {
        id = (Integer) source.readValue(Integer.class.getClassLoader());
        name = source.readString();
        about = source.readString();
        store_url = source.readString();
        avatar = source.readParcelable(UserAvatar.class.getClassLoader());
        isOwner = (Boolean) source.readValue(Boolean.class.getClassLoader());
        isFollowing = (Boolean) source.readValue(Boolean.class.getClassLoader());
        badgeCount = (Integer) source.readValue(Integer.class.getClassLoader());
    }

    public String getAvatarUrl() {
        if (getAvatar() != null && getAvatar().getAvatar() != null && !TextUtils.isEmpty(getAvatar().getAvatar().getUrl())) {
            return getAvatar().getAvatar().getUrl();
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(UserAvatar avatar) {
        this.avatar = avatar;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public Boolean getIsFollowing() {
        if (isFollowing == null) return false;
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public Integer getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(Integer badgeCount) {
        this.badgeCount = badgeCount;
    }

    public String getStore_url() {
        return store_url;
    }

    @Override
    public PostReceiverType getType() {
        return PostReceiverType.BUSINESS;
    }

    @Override
    public Integer getReceiverId() {
        return getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeString(name);
        dest.writeString(about);
        dest.writeString(store_url);
        dest.writeParcelable(avatar, flags);
        dest.writeValue(isOwner);
        dest.writeValue(isFollowing);
        dest.writeValue(badgeCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Business)) {
            return false;
        }

        Business business = (Business) o;

        if (about != null ? !about.equals(business.about) : business.about != null) {
            return false;
        }
        if (avatar != null ? !avatar.equals(business.avatar) : business.avatar != null) {
            return false;
        }
        if (badgeCount != null ? !badgeCount.equals(business.badgeCount)
                : business.badgeCount != null) {
            return false;
        }
        if (id != null ? !id.equals(business.id) : business.id != null) {
            return false;
        }
        if (isFollowing != null ? !isFollowing.equals(business.isFollowing)
                : business.isFollowing != null) {
            return false;
        }
        if (isOwner != null ? !isOwner.equals(business.isOwner) : business.isOwner != null) {
            return false;
        }
        if (name != null ? !name.equals(business.name) : business.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (isOwner != null ? isOwner.hashCode() : 0);
        result = 31 * result + (isFollowing != null ? isFollowing.hashCode() : 0);
        result = 31 * result + (badgeCount != null ? badgeCount.hashCode() : 0);
        return result;
    }
}
