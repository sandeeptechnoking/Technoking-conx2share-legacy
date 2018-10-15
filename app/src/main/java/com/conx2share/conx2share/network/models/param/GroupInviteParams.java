package com.conx2share.conx2share.network.models.param;

import com.google.gson.annotations.SerializedName;

import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.models.User;

public class GroupInviteParams {

    @SerializedName("group_id")
    private Integer groupId;

    @SerializedName("invited_user_id")
    private Integer invitedUserId;

    public GroupInviteParams() {
    }

    public GroupInviteParams(int groupId, int invitedUserId) {

        this.groupId = groupId;
        this.invitedUserId = invitedUserId;
    }

    public GroupInviteParams(Group group, User invitedUser) {
        setGroupId(group.getId());
        setInvitedUserId(invitedUser.getId());
    }

    public int getGroupId() {

        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(int invitedUserId) {
        this.invitedUserId = invitedUserId;
    }
}
