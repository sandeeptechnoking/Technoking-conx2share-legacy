package com.conx2share.conx2share.network.models.response;


public class GroupLeaveResponse {

    private int id;

    private int userId;

    private int groupId;

    private String createdAt;

    private String updatedAt;

    public GroupLeaveResponse() {

    }

    public GroupLeaveResponse(int id, int userId, int groupId, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
