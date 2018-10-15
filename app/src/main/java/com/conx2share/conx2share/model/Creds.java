package com.conx2share.conx2share.model;

import com.google.gson.annotations.SerializedName;

public class Creds {

    @SerializedName("api_user")
    private ApiUser mApiUser;

    public Creds(ApiUser apiUser) {
        this.mApiUser = apiUser;
    }

    public ApiUser getApiUser() {
        return mApiUser;
    }
}
