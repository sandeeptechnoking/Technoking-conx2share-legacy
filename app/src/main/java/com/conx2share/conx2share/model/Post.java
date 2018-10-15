package com.conx2share.conx2share.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.response.HashTag;

import com.conx2share.conx2share.network.models.UserTag;

import java.util.ArrayList;
import java.util.Date;

public class Post extends TagHolder implements Parcelable {

    private Integer id;
    private Integer userId;
    private Integer likesCount;
    private Date createdAt;
    private PostPicture picture;
    private String userAvatar;
    private String userFirstName;
    private String userLastName;
    private boolean hasLiked;
    private Video video;
    private StreamingUrl streamingUrl;
    private int commentCount;
    private String groupName;
    private Integer groupId;
    private String businessName;
    private int businessId;
    private String businessImage;
    private String userUsername;
    private String groupImage;
    private Integer groupStatus;
    private ArrayList<Like> likes;
    private boolean is_private;

    public Post() {
        // NO OP
    }

    public Post(int id, int userId, String body, int likesCount, Date createdAt, PostPicture picture, String
            userAvatar, String userFirstName, String userLastName, boolean hasLiked, Video video, StreamingUrl
            streamingUrl, int commentCount) {
        this.id = id;
        this.userId = userId;
        this.body = body;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.picture = picture;
        this.userAvatar = userAvatar;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.hasLiked = hasLiked;
        this.video = video;
        this.streamingUrl = streamingUrl;
        this.commentCount = commentCount;
    }

    public Post(int id, int userId, String body, int likesCount, Date createdAt, PostPicture picture, String
            userAvatar, String userFirstName, String userLastName, boolean hasLiked, Video video, StreamingUrl
            streamingUrl, int commentCount, Integer groupStatus) {
        this.id = id;
        this.userId = userId;
        this.body = body;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.picture = picture;
        this.userAvatar = userAvatar;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.hasLiked = hasLiked;
        this.video = video;
        this.streamingUrl = streamingUrl;
        this.commentCount = commentCount;
        this.groupStatus = groupStatus;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", body='" + body + '\'' +
                ", createdAt=" + createdAt +
                ", likesCount=" + likesCount +
                ", picture=" + picture.toString() +
                ", userAvatar='" + userAvatar + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", hasLiked=" + hasLiked +
                ", video=" + video.toString() +
                ", streamingUrl=" + streamingUrl.toString() +
                ", commentCount=" + commentCount +
                ", groupName='" + groupName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessId'" + businessId + '\'' +
                '}';
    }

    public boolean hasImage() {
        return getPicture() != null && getPicture().getPicture() != null && getPicture().getPicture().getUrl() !=
                null && !TextUtils.isEmpty(getPicture().getPicture().getUrl());
    }

    public boolean hasVideo() {
        return getVideo() != null && getVideo().getVideo() != null && getVideo().getVideo().getUrl() != null &&
                !TextUtils.isEmpty(getVideo().getVideo().getUrl());
    }

    public String getImageUrl() {
        if (!hasImage()) {
            return null;
        }

        return getPicture().getPicture().getFeedUrl();
    }

    public String getFullImageUrl() {
        if (!hasImage()) {
            return null;
        }

        return getPicture().getPicture().getUrl();
    }

    public String getThumbImageUrl() {
        if (!hasImage()) {
            return null;
        }
        return getPicture().getPicture().getThumbUrl();
    }

    public String getVideoUrl() {
        if (!hasVideo()) {
            return null;
        }

        return getVideo().getVideo().getUrl();
    }

    public String getShareToTwitterString(Resources res) {
        return res.getString(R.string.shared_by_text) + getUserFirstName() + " " + getUserLastName() + res.getString
                (R.string.via_conx2share_android_text);
    }

    public String getShareToFacebookString(Resources res) {
        return "\"" + getBody() + res.getString(R.string.shared_by_text) + getUserFirstName() + " " + getUserLastName
                () + res.getString(R.string.via_conx2share_android_text);
    }

    public boolean IsPrivate() {
        return is_private;
    }

    public void setIsPrivate(boolean is_private) {
        this.is_private = is_private;
    }

    public String getUserDisplayName() {
        if (isBusinessPost()) {
            return getBusinessName();
        } else if (isGroupPost()) {
            return getGroupName();
        } else {
            return getUserFirstName() + " " + getUserLastName();
        }
    }

    public String getAvatarUrl() {
        if (isBusinessPost()) {
            return getBusinessImage();
        } else if (isGroupPost()) {
            return getGroupImage();
        } else {
            return getUserAvatar();
        }
    }

    public boolean isBusinessPost() {
        return !TextUtils.isEmpty(getBusinessName());
    }

    public boolean isGroupPost() {
        return !TextUtils.isEmpty(getGroupName());
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public PostPicture getPicture() {
        return picture;
    }

    public void setPicture(PostPicture picture) {
        this.picture = picture;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public boolean getHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public StreamingUrl getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(StreamingUrl streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getUsername() {
        return userUsername;
    }

    public void setUsername(String username) {
        this.userUsername = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }

        Post post = (Post) o;

        if (businessId != post.businessId) {
            return false;
        }
        if (commentCount != post.commentCount) {
            return false;
        }
        if (groupId != post.groupId) {
            return false;
        }
        if (hasLiked != post.hasLiked) {
            return false;
        }
        if (body != null ? !body.equals(post.body) : post.body != null) {
            return false;
        }
        if (businessImage != null ? !businessImage.equals(post.businessImage) : post.businessImage != null) {
            return false;
        }
        if (businessName != null ? !businessName.equals(post.businessName) : post.businessName != null) {
            return false;
        }
        if (createdAt != null ? !createdAt.equals(post.createdAt) : post.createdAt != null) {
            return false;
        }
        if (groupName != null ? !groupName.equals(post.groupName) : post.groupName != null) {
            return false;
        }
        if (groupStatus != null ? !groupStatus.equals(post.groupStatus) : post.groupStatus != null) {
            return false;
        }
        if (id != null ? !id.equals(post.id) : post.id != null) {
            return false;
        }
        if (likesCount != null ? !likesCount.equals(post.likesCount) : post.likesCount != null) {
            return false;
        }
        if (picture != null ? !picture.equals(post.picture) : post.picture != null) {
            return false;
        }
        if (streamingUrl != null ? !streamingUrl.equals(post.streamingUrl) : post.streamingUrl != null) {
            return false;
        }
        if (userAvatar != null ? !userAvatar.equals(post.userAvatar) : post.userAvatar != null) {
            return false;
        }
        if (userFirstName != null ? !userFirstName.equals(post.userFirstName) : post.userFirstName != null) {
            return false;
        }
        if (userId != null ? !userId.equals(post.userId) : post.userId != null) {
            return false;
        }
        if (userLastName != null ? !userLastName.equals(post.userLastName) : post.userLastName != null) {
            return false;
        }
        if (video != null ? !video.equals(post.video) : post.video != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (likesCount != null ? likesCount.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (userAvatar != null ? userAvatar.hashCode() : 0);
        result = 31 * result + (userFirstName != null ? userFirstName.hashCode() : 0);
        result = 31 * result + (userLastName != null ? userLastName.hashCode() : 0);
        result = 31 * result + (hasLiked ? 1 : 0);
        result = 31 * result + (video != null ? video.hashCode() : 0);
        result = 31 * result + (streamingUrl != null ? streamingUrl.hashCode() : 0);
        result = 31 * result + commentCount;
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + groupId;
        result = 31 * result + (businessName != null ? businessName.hashCode() : 0);
        result = 31 * result + businessId;
        result = 31 * result + (groupStatus != null ? groupStatus.hashCode() : 0);
        result = 31 * result + (businessImage != null ? businessImage.hashCode() : 0);
        return result;
    }

    public String getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(String businessImage) {
        this.businessImage = businessImage;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public ArrayList<Like> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        this.likes = likes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeValue(this.id);
        dest.writeValue(this.userId);
        dest.writeValue(this.likesCount);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeParcelable(this.picture, flags);
        dest.writeString(this.userAvatar);
        dest.writeString(this.userFirstName);
        dest.writeString(this.userLastName);
        dest.writeByte(this.hasLiked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.video, flags);
        dest.writeParcelable(this.streamingUrl, flags);
        dest.writeInt(this.commentCount);
        dest.writeString(this.groupName);
        dest.writeValue(this.groupId);
        dest.writeString(this.businessName);
        dest.writeInt(this.businessId);
        dest.writeString(this.businessImage);
        dest.writeString(this.userUsername);
        dest.writeString(this.groupImage);
        dest.writeValue(this.groupStatus);
        dest.writeTypedList(this.likes);
        dest.writeByte(this.is_private ? (byte) 1 : (byte) 0);
    }

    protected Post(Parcel in) {
        this.body = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likesCount = (Integer) in.readValue(Integer.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.picture = in.readParcelable(PostPicture.class.getClassLoader());
        this.userAvatar = in.readString();
        this.userFirstName = in.readString();
        this.userLastName = in.readString();
        this.hasLiked = in.readByte() != 0;
        this.video = in.readParcelable(Video.class.getClassLoader());
        this.streamingUrl = in.readParcelable(StreamingUrl.class.getClassLoader());
        this.commentCount = in.readInt();
        this.groupName = in.readString();
        this.groupId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.businessName = in.readString();
        this.businessId = in.readInt();
        this.businessImage = in.readString();
        this.userUsername = in.readString();
        this.groupImage = in.readString();
        this.groupStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likes = in.createTypedArrayList(Like.CREATOR);
        this.is_private = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
