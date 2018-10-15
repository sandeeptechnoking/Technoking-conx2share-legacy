package com.conx2share.conx2share.network.models.param;

import com.google.gson.annotations.SerializedName;

public class PostChatParams {

    @SerializedName("user_id")
    private String userId;

    public PostChatParams(String userId) {
        setUserId(userId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
