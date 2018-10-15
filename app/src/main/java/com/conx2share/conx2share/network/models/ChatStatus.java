package com.conx2share.conx2share.network.models;

import com.google.gson.annotations.SerializedName;

public enum ChatStatus {
    @SerializedName("active")
    active,

    @SerializedName("fresh")
    FRESH,

    @SerializedName("closed")
    CLOSED,

    @SerializedName("report")
    REPORT
}