package com.conx2share.conx2share.network.models.param;

import com.google.gson.annotations.SerializedName;

public class GroupLeaveParams {

    @SerializedName("id")
    private int id;

    public GroupLeaveParams(int id) {
        setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}