package com.conx2share.conx2share.network.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.StreamingUrl;
import com.conx2share.conx2share.model.Picture;
import com.conx2share.conx2share.model.UserAvatar;
import com.conx2share.conx2share.model.UserCover;
import com.linkedin.android.spyglass.mentions.Mentionable;

import java.util.ArrayList;

public class User implements Parcelable, Mentionable {

    private int id;
    private String password;
    private String currentPassword;
    private String passwordConfirmation;
    private String birthday;
    private String firstName;
    private String lastName;
    private String username;

    private String email;

    private UserAvatar avatar;
    private UserCover coverPhoto;

    private boolean isFollowing, isFollower;
    private Integer followers;

    private Integer following;

    private String groupStatus;

    private boolean groupFollowStatus;

    private String plan;

    private String about;

    private boolean promoUser;

    private boolean isFavorite;

    private boolean isBlocked;

    private StreamingUrl liveStream;

    private Boolean messageNotifications;

    private Boolean tagNotifications;

    private Boolean postNotifications;

    private Boolean inviteNotifications;

    private Boolean newPostNotifications;

    private Boolean followerNotifications;

    private String mPasswordConfirmation;

    private Boolean overEighteen;

    private ArrayList<Post> recentPosts;

    public User() {

    }

    public User(int id, String birthday, String firstName, String lastName, String username, String email, UserAvatar
            avatar, boolean isFollowing, boolean isFavorite) {
        this.id = id;
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.isFollowing = isFollowing;
    }

    public User(int id, String birthday, String firstName, String lastName, String username, String email, UserAvatar
            avatar, boolean isFollowing) {
        this.id = id;
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.isFollowing = isFollowing;
    }

    public User(int id, String birthday, String firstName, String lastName, String username, String email, UserAvatar
            avatar, boolean isFollowing, Integer following, Integer followers, String groupStatus) {
        this.id = id;
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.isFollowing = isFollowing;
        this.followers = followers;
        this.following = following;
        this.groupStatus = groupStatus;
    }

    public User(String email, String username, String about, Boolean messageNotifications, Boolean tagNotifications,
                Boolean postNotifications, Boolean inviteNotifications, Boolean newpostNotifications, Boolean
                        followerNotifications) {
        this.username = username;
        this.email = email;
        this.about = about;
        this.messageNotifications = messageNotifications;
        this.tagNotifications = tagNotifications;
        this.postNotifications = postNotifications;
        this.inviteNotifications = inviteNotifications;
        this.newPostNotifications = newpostNotifications;
        this.followerNotifications = followerNotifications;
    }

    public User(int id, String firstName, String lastName, boolean isFollowing, boolean groupFollowStatus, boolean
            isBlocked,  StreamingUrl liveStream, UserAvatar avatar) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isFollowing = isFollowing;
        this.groupFollowStatus = groupFollowStatus;
        this.isBlocked = isBlocked;
        this.liveStream = liveStream;
        this.avatar = avatar;
    }

    public User(String password) {
        this.password = password;
    }

    public User(boolean isFollowing, boolean isFavorite) {
        this.isFollowing = isFollowing;
        this.isFavorite = isFavorite;
    }

    public User(String firstName, String lastName, boolean isFollowing) {//primarily for testing
        this.firstName = firstName;
        this.lastName = lastName;
        this.isFollowing = isFollowing;
        setAvatar(new UserAvatar(new Picture("")));
    }

    @Override
    public String toString() {
        // password-related fields intentionally removed from generated toString() method
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", avatar=" + avatar +
                ", isFollowing=" + isFollowing +
                ", isFollower=" + isFollower +
                ", followers=" + followers +
                ", following=" + following +
                ", groupStatus='" + groupStatus + '\'' +
                ", plan='" + plan + '\'' +
                ", about='" + about + '\'' +
                ", promoUser=" + promoUser +
                ", isFavorite=" + isFavorite +
                '}';
    }

    public ArrayList<Post> getRecentPosts() {
        return recentPosts;
    }

    public void setRecentPosts(ArrayList<Post> recent_posts) {
        this.recentPosts = recent_posts;
    }

    public String getDisplayName() {
        return getFirstName() + ' ' + getLastName();
    }

    public String getHandleText() {
        return "@" + getUsername();
    }

    public String getAvatarUrl() {
        if (getAvatar() != null && !TextUtils.isEmpty(getAvatar().getUrl())) {
            return getAvatar().getUrl();
        } else if (getAvatar() != null && getAvatar().getAvatar() != null && !TextUtils.isEmpty(getAvatar().getAvatar
                ().getUrl())) {
            return getAvatar().getAvatar().getUrl();
        } else {
            return null;
        }
    }

    @Override
    public String getPrimaryText() {
        return getDisplayName();
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mentionDisplayMode) {
        return getHandleText();
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.FULL_DELETE;
    }

    public UserCover getCover() {
        return coverPhoto;
    }

    public void setCover(UserCover cover_photo) {
        this.coverPhoto = cover_photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(UserAvatar avatar) {
        this.avatar = avatar;
    }

    public boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean isFollower) {
        this.isFollower = isFollower;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPromoUser() {
        return promoUser;
    }

    public void setPromoUser(boolean promoUser) {
        this.promoUser = promoUser;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public StreamingUrl getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(StreamingUrl liveStream) {
        this.liveStream = liveStream;
    }

    public boolean getGroupFollowStatus() {
        return groupFollowStatus;
    }

    public void setGroupFollowStatus(boolean groupFollowStatus) {
        this.groupFollowStatus = groupFollowStatus;
    }

    public Boolean getFollowerNotifications() {
        return followerNotifications;
    }

    public void setFollowerNotifications(Boolean followerNotifications) {
        this.followerNotifications = followerNotifications;
    }

    public Boolean getTagNotifications() {
        return tagNotifications;
    }

    public void setTagNotifications(Boolean tagNotifications) {
        this.tagNotifications = tagNotifications;
    }

    public Boolean getPostNotifications() {
        return postNotifications;
    }

    public void setPostNotifications(Boolean postNotifications) {
        this.postNotifications = postNotifications;
    }

    public Boolean getInviteNotifications() {
        return inviteNotifications;
    }

    public void setInviteNotifications(Boolean inviteNotifications) {
        this.inviteNotifications = inviteNotifications;
    }

    public Boolean getNewPostNotifications() {
        return newPostNotifications;
    }

    public void setNewpostNotifications(Boolean newPostNotifications) {
        this.newPostNotifications = newPostNotifications;
    }

    public Boolean getMessageNotifications() {
        return messageNotifications;
    }

    public void setMessageNotifications(Boolean messageNotifications) {
        this.messageNotifications = messageNotifications;
    }

    public Boolean isOverEighteen() {
        return overEighteen;
    }

    public void setOverEighteen(Boolean overEighteen) {
        this.overEighteen = overEighteen;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        mPasswordConfirmation = passwordConfirmation;
    }

    /**
     *
     * @return {@code true} if both users are following each other, {@code false} otherwise
     */
    public boolean isMutualFollower() {
        return (isFollower && isFollowing);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        if (id != user.id) {
            return false;
        }
        if (isFollowing != user.isFollowing) {
            return false;
        }
        if (isFollower != user.isFollower) {
            return false;
        }
        if (groupFollowStatus != user.groupFollowStatus) {
            return false;
        }
        if (promoUser != user.promoUser) {
            return false;
        }
        if (isFavorite != user.isFavorite) {
            return false;
        }
        if (isBlocked != user.isBlocked) {
            return false;
        }

        if (liveStream != null ? !liveStream.equals(user.liveStream) : user.liveStream != null) {
            return false;
        }

        if (password != null ? !password.equals(user.password) : user.password != null) {
            return false;
        }
        if (currentPassword != null ? !currentPassword.equals(user.currentPassword) : user.currentPassword != null) {
            return false;
        }
        if (passwordConfirmation != null ? !passwordConfirmation.equals(user.passwordConfirmation) : user
                .passwordConfirmation != null) {
            return false;
        }
        if (birthday != null ? !birthday.equals(user.birthday) : user.birthday != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) {
            return false;
        }
        if (username != null ? !username.equals(user.username) : user.username != null) {
            return false;
        }
        if (email != null ? !email.equals(user.email) : user.email != null) {
            return false;
        }
        if (avatar != null ? !avatar.equals(user.avatar) : user.avatar != null) {
            return false;
        }
        if (followers != null ? !followers.equals(user.followers) : user.followers != null) {
            return false;
        }
        if (following != null ? !following.equals(user.following) : user.following != null) {
            return false;
        }
        if (groupStatus != null ? !groupStatus.equals(user.groupStatus) : user.groupStatus != null) {
            return false;
        }
        if (plan != null ? !plan.equals(user.plan) : user.plan != null) {
            return false;
        }
        if (about != null ? !about.equals(user.about) : user.about != null) {
            return false;
        }
        if (messageNotifications != null ? !messageNotifications.equals(user.messageNotifications) : user
                .messageNotifications != null) {
            return false;
        }
        if (tagNotifications != null ? !tagNotifications.equals(user.tagNotifications) : user.tagNotifications !=
                null) {
            return false;
        }
        if (postNotifications != null ? !postNotifications.equals(user.postNotifications) : user.postNotifications !=
                null) {
            return false;
        }
        if (inviteNotifications != null ? !inviteNotifications.equals(user.inviteNotifications) : user
                .inviteNotifications != null) {
            return false;
        }
        if (newPostNotifications != null ? !newPostNotifications.equals(user.newPostNotifications) : user
                .newPostNotifications != null) {
            return false;
        }
        if (followerNotifications != null ? !followerNotifications.equals(user.followerNotifications) : user
                .followerNotifications != null) {
            return false;
        }
        if (mPasswordConfirmation != null ? !mPasswordConfirmation.equals(user.mPasswordConfirmation) : user
                .mPasswordConfirmation != null) {
            return false;
        }
        return !(overEighteen != null ? !overEighteen.equals(user.overEighteen) : user.overEighteen != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (currentPassword != null ? currentPassword.hashCode() : 0);
        result = 31 * result + (passwordConfirmation != null ? passwordConfirmation.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (isFollowing ? 1 : 0);
        result = 31 * result + (isFollower ? 1 : 0);
        result = 31 * result + (followers != null ? followers.hashCode() : 0);
        result = 31 * result + (following != null ? following.hashCode() : 0);
        result = 31 * result + (groupStatus != null ? groupStatus.hashCode() : 0);
        result = 31 * result + (groupFollowStatus ? 1 : 0);
        result = 31 * result + (plan != null ? plan.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        result = 31 * result + (promoUser ? 1 : 0);
        result = 31 * result + (isFavorite ? 1 : 0);
        result = 31 * result + (isBlocked ? 1 : 0);
        result = 31 * result + (liveStream != null ? liveStream.hashCode() : 0);
        result = 31 * result + (messageNotifications != null ? messageNotifications.hashCode() : 0);
        result = 31 * result + (tagNotifications != null ? tagNotifications.hashCode() : 0);
        result = 31 * result + (postNotifications != null ? postNotifications.hashCode() : 0);
        result = 31 * result + (inviteNotifications != null ? inviteNotifications.hashCode() : 0);
        result = 31 * result + (newPostNotifications != null ? newPostNotifications.hashCode() : 0);
        result = 31 * result + (followerNotifications != null ? followerNotifications.hashCode() : 0);
        result = 31 * result + (mPasswordConfirmation != null ? mPasswordConfirmation.hashCode() : 0);
        result = 31 * result + (overEighteen != null ? overEighteen.hashCode() : 0);
        return result;
    }

    public boolean isUserMatchQuery(@NonNull String keyword){
        if (TextUtils.isEmpty(keyword)) return false;
        keyword = keyword.toLowerCase();
        return firstName.toLowerCase().startsWith(keyword)
                || lastName.toLowerCase().startsWith(keyword)
                || username.toLowerCase().startsWith(keyword)
                || email.toLowerCase().startsWith(keyword);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.password);
        dest.writeString(this.currentPassword);
        dest.writeString(this.passwordConfirmation);
        dest.writeString(this.birthday);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeParcelable(this.avatar, flags);
        dest.writeParcelable(this.coverPhoto, flags);
        dest.writeByte(this.isFollowing ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFollower ? (byte) 1 : (byte) 0);
        dest.writeValue(this.followers);
        dest.writeValue(this.following);
        dest.writeString(this.groupStatus);
        dest.writeByte(this.groupFollowStatus ? (byte) 1 : (byte) 0);
        dest.writeString(this.plan);
        dest.writeString(this.about);
        dest.writeByte(this.promoUser ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isBlocked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.liveStream, 0);
        dest.writeValue(this.messageNotifications);
        dest.writeValue(this.tagNotifications);
        dest.writeValue(this.postNotifications);
        dest.writeValue(this.inviteNotifications);
        dest.writeValue(this.newPostNotifications);
        dest.writeValue(this.followerNotifications);
        dest.writeString(this.mPasswordConfirmation);
        dest.writeValue(this.overEighteen);
        dest.writeTypedList(this.recentPosts);
    }

    protected User(Parcel in) {
        this.id = in.readInt();
        this.password = in.readString();
        this.currentPassword = in.readString();
        this.passwordConfirmation = in.readString();
        this.birthday = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.avatar = in.readParcelable(UserAvatar.class.getClassLoader());
        this.coverPhoto = in.readParcelable(UserCover.class.getClassLoader());
        this.isFollowing = in.readByte() != 0;
        this.isFollower = in.readByte() != 0;
        this.followers = (Integer) in.readValue(Integer.class.getClassLoader());
        this.following = (Integer) in.readValue(Integer.class.getClassLoader());
        this.groupStatus = in.readString();
        this.groupFollowStatus = in.readByte() != 0;
        this.plan = in.readString();
        this.about = in.readString();
        this.promoUser = in.readByte() != 0;
        this.isFavorite = in.readByte() != 0;
        this.isBlocked = in.readByte() != 0;
        this.messageNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.tagNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.postNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.inviteNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.newPostNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.followerNotifications = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mPasswordConfirmation = in.readString();
        this.overEighteen = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.recentPosts = in.createTypedArrayList(Post.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
