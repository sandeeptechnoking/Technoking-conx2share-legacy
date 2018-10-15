package com.conx2share.conx2share.network.models.param;

import android.location.Location;
import android.support.annotation.Nullable;

import com.conx2share.conx2share.network.models.ChatStatus;
import com.google.gson.annotations.SerializedName;

public class SayNoGroup {
    @SerializedName("is_incident")
    private boolean isIncident;

    @SerializedName("group_id")
    private int groupId;

    @SerializedName("is_anonymous")
    private boolean isAnonymous;

    private ChatStatus status;
    private Double latitude;
    private Double longitude;

    public SayNoGroup(boolean isIncident, int groupId, @Nullable Location location, ChatStatus status) {
        this.isIncident = isIncident;
        this.groupId = groupId;
        this.latitude = location == null ? null : location.getLatitude();
        this.longitude = location == null ? null : location.getLongitude();
        this.isAnonymous = isAnonymous;
        this.status = status;
    }

    public boolean isIncident() {
        return isIncident;
    }

    public void setIncident(boolean incident) {
        isIncident = incident;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
