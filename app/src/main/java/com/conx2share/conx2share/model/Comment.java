package com.conx2share.conx2share.model;

import com.google.gson.annotations.SerializedName;

import com.conx2share.conx2share.network.models.UserTag;

import java.util.ArrayList;
import java.util.Date;

public class Comment extends TagHolder {

    private Integer id;
    
    private Date createdAt;

    private Integer commentableId;

    private String commentableType;

    private Integer commenterId;

    private String commenterFirstName;

    private String commenterLastName;

    private String commenterAvatar;

    private Integer groupStatus;

    private String commenterUsername;


    /** This field has a different name when submitting user tags to the server vs. receiving them from the server.  The base TagHolder has the field for tags received.  */
    @SerializedName("user_tags_attributes")
    private ArrayList<UserTag> userTagsToSendToServer;

    public Comment(Integer id, String body, Date created_at, Integer commentable_id, String commentable_type, Integer commenter_id, String commenter_first_name, String commenter_last_name,
            String commenter_avatar) {
        this.id = id;
        this.body = body;
        createdAt = created_at;
        commentableId = commentable_id;
        commentableType = commentable_type;
        commenterId = commenter_id;
        commenterFirstName = commenter_first_name;
        commenterLastName = commenter_last_name;
        commenterAvatar = commenter_avatar;
    }

    public Comment(Integer id, String body, Date created_at, Integer commentable_id, String commentable_type, Integer commenter_id, String commenter_first_name, String commenter_last_name,
            String commenter_avatar, Integer groupStatus) {
        this.id = id;
        this.body = body;
        this.createdAt = created_at;
        this.commentableId = commentable_id;
        this.commentableType = commentable_type;
        this.commenterId = commenter_id;
        this.commenterFirstName = commenter_first_name;
        this.commenterLastName = commenter_last_name;
        this.commenterAvatar = commenter_avatar;
        this.groupStatus = groupStatus;
    }

    public Comment(String body, Integer commentableId, String commentableType) {
        this.body = body;
        this.commentableId = commentableId;
        this.commentableType = commentableType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCommentableId() {
        return commentableId;
    }

    public void setCommentableId(Integer commentableId) {
        this.commentableId = commentableId;
    }

    public String getCommentableType() {
        return commentableType;
    }

    public void setCommentableType(String commentableType) {
        this.commentableType = commentableType;
    }

    public Integer getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(Integer commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommenterFirstName() {
        return commenterFirstName;
    }

    public void setCommenterFirstName(String commenterFirstName) {
        this.commenterFirstName = commenterFirstName;
    }

    public String getCommenterLastName() {
        return commenterLastName;
    }

    public void setCommenterLastName(String commenterLastName) {
        this.commenterLastName = commenterLastName;
    }

    public String getCommenterAvatar() {
        return commenterAvatar;
    }

    public void setCommenterAvatar(String commenterAvatar) {
        this.commenterAvatar = commenterAvatar;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getCommenterHandle() {
        return commenterUsername;
    }

    public void setCommenterHandle(String commenterHandle) {
        this.commenterUsername = commenterHandle;
    }

    public ArrayList<UserTag> getUserTagsToSendToServer() {
        return userTagsToSendToServer;
    }

    public void setUserTagsToSendToServer(ArrayList<UserTag> userTagsToSendToServer) {
        this.userTagsToSendToServer = userTagsToSendToServer;
    }
}
