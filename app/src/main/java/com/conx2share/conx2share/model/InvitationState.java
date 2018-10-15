package com.conx2share.conx2share.model;

import com.google.gson.annotations.SerializedName;

public enum InvitationState {
    @SerializedName("pending")
    PENDING,
    @SerializedName("fresh")
    FRESH,
    @SerializedName("active")
    ACTIVE,
    @SerializedName("closed")
    CLOSED,
    @SerializedName("report")
    REPORT,
    @SerializedName("accepted")
    ACCEPTED,
    @SerializedName("declined")
    DECLINED
}