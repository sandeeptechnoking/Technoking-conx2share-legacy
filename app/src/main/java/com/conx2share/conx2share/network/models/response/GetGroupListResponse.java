package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Group;

import java.util.ArrayList;

public class GetGroupListResponse {

    private ArrayList<Group> groups;

    public GetGroupListResponse(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}
