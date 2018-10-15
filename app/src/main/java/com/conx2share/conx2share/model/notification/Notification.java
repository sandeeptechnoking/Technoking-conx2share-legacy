package com.conx2share.conx2share.model.notification;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {

    public final static int MessageNew = 0;
    public final static int MessageRead = 1;
    public final static int CommentNew = 2;
    public final static int FollowerNew = 3;
    public final static int GroupInvite = 4;
    public final static int PostUserNew = 5;
    public final static int PostGroupNew = 6;
    public final static int GroupFollowInvite = 8;
    public final static int TaggedPostNew = 9;
    public final static int TaggedCommentNew = 10;
    public final static int PostBusinessNew = 11;
    public final static int EventStarting = 12;
    public final static int FollowerInvite = 13;
    public final static int LiveStreamStarting = 14;
    public final static int SayNoGroupInviteAccepted = 15;
    public final static int EventCreated = 16;

    private Integer id;
    private Integer notificationType;
    private Integer notificationObjectId;
    private String notificationObjectType;
    private boolean viewed;
    private Date createdAt;
    private String notificationImage;
    private String notificationObjectName;
    private NotificationStatus groupStatus;

    public enum NotificationStatus{
        @SerializedName("pending")
        PENDING,
        @SerializedName("accepted")
        ACCEPTED,
        @SerializedName("declined")
        DECLINED
    }

    public Notification(Integer id, Integer notificationType, Integer notificationObjectId, String notificationObjectType, boolean viewed, Date createdAt, String notificationImage,
            String notificationObjectName) {
        this.id = id;
        this.notificationType = notificationType;
        this.notificationObjectId = notificationObjectId;
        this.notificationObjectType = notificationObjectType;
        this.viewed = viewed;
        this.createdAt = createdAt;
        this.notificationImage = notificationImage;
        this.notificationObjectName = notificationObjectName;
    }

    public Notification(Integer id, Integer notificationType, Integer notificationObjectId, String notificationObjectType, boolean viewed, Date createdAt, String notificationImage,
            String notificationObjectName, NotificationStatus groupStatus) {
        this.id = id;
        this.notificationType = notificationType;
        this.notificationObjectId = notificationObjectId;
        this.notificationObjectType = notificationObjectType;
        this.viewed = viewed;
        this.createdAt = createdAt;
        this.notificationImage = notificationImage;
        this.notificationObjectName = notificationObjectName;
        this.groupStatus = groupStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getNotificationObjectId() {
        return notificationObjectId;
    }

    public void setNotificationObjectId(Integer notificationObjectId) {
        this.notificationObjectId = notificationObjectId;
    }

    public String getNotificationObjectType() {
        return notificationObjectType;
    }

    public void setNotificationObjectType(String notificationObjectType) {
        this.notificationObjectType = notificationObjectType;
    }

    public void setNotificationObjecType(String notificationObjectType) {
        this.notificationObjectType = notificationObjectType;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    public String getNotificationObjectName() {
        return notificationObjectName;
    }

    public void setNotificationObjectName(String notificationObjectName) {
        this.notificationObjectName = notificationObjectName;
    }

    public NotificationStatus getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(NotificationStatus groupStatus) {
        this.groupStatus = groupStatus;
    }
}
