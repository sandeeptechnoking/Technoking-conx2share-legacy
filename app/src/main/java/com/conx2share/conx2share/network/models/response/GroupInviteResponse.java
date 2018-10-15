package com.conx2share.conx2share.network.models.response;

import com.google.gson.annotations.SerializedName;

public class GroupInviteResponse {

    private Integer id;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("group_id")
    private Integer groupId;


    private String state;

    public GroupInviteResponse() {

    }

    public GroupInviteResponse(Integer id, Integer userId, Integer groupId, String state) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
