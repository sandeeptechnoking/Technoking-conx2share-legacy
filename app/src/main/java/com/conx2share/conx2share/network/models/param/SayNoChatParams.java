package com.conx2share.conx2share.network.models.param;

import android.location.Location;
import android.support.annotation.Nullable;

import com.conx2share.conx2share.network.models.ChatStatus;
import com.google.gson.annotations.SerializedName;

public class SayNoChatParams {
    @SerializedName("chat")
    private final SayNoGroup group;

    @SerializedName("is_anonymous")
    private boolean isAnonymous;

    private SayNoChatParams(SayNoGroup group, boolean isAnonymous) {
        this.group = group;
        this.isAnonymous = isAnonymous;
    }

    public static SayNoChatParams createChat(boolean isAnonymous,
                                             int groupId,
                                             @Nullable Location location) {
        return new SayNoChatParams(new SayNoGroup(true, groupId, location, ChatStatus.FRESH), isAnonymous);
    }

    public static SayNoChatParams createReport(boolean isAnonymous,
                                               int groupId,
                                               @Nullable Location location) {
        return new SayNoChatParams(new SayNoGroup(true, groupId, location, ChatStatus.REPORT), isAnonymous);
    }
}