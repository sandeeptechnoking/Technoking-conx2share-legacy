package com.conx2share.conx2share.model.notification;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationEntity {

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

    private String title;
    private String body;
    private boolean viewed;
    private int id;
    @SerializedName("click_type")
    private int clickType;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("reused_at")
    private Date reusedAt;
    @SerializedName("click_data")
    private NotificationData clickData;


    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public int getId() {
        return id;
    }

    public int getClickType() {
        return clickType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getReusedAt() {
        return reusedAt;
    }

    public NotificationData getClickData() {
        return clickData;
    }


}
