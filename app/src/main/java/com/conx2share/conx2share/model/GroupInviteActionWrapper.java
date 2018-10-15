package com.conx2share.conx2share.model;

public class GroupInviteActionWrapper {

    private String groupId;

    public GroupInviteActionWrapper(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
