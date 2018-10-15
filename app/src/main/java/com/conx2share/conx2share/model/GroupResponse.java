package com.conx2share.conx2share.model;

public class GroupResponse {

    private Group group;

    public GroupResponse() {

    }

    public GroupResponse(Group group) {
        setGroup(group);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
