package com.conx2share.conx2share.model.notification;


import com.google.gson.annotations.SerializedName;

public class NotificationData {

    @SerializedName("user_id")
    private int userId;
    @SerializedName("post_id")
    private int postId;
    @SerializedName("comment_id")
    private int commentId;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("event_id")
    private int eventId;
    @SerializedName("message_id")
    private int messageId;
    @SerializedName("chat_id")
    private int chatId;
    @SerializedName("user_invite_status")
    private InviteStatus userInviteStatus;
    @SerializedName("group_invite_status")
    private InviteStatus groupInviteStatus;


    public enum InviteStatus{
        @SerializedName("pending")
        PENDING,
        @SerializedName("accepted")
        ACCEPTED,
        @SerializedName("declined")
        DECLINED
    }

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getChatId() {
        return chatId;
    }

    public InviteStatus getUserInviteStatus() {
        return userInviteStatus;
    }

    public InviteStatus getGroupInviteStatus() {
        return groupInviteStatus;
    }

    public void setUserInviteStatus(InviteStatus userInviteStatus) {
        this.userInviteStatus = userInviteStatus;
    }

    public void setGroupInviteStatus(InviteStatus groupInviteStatus) {
        this.groupInviteStatus = groupInviteStatus;
    }
}
