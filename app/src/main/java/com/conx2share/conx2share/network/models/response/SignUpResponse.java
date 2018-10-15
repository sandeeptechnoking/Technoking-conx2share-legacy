package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.AuthUser;

public class SignUpResponse {

    private AuthUser auth_user;

    public SignUpResponse(AuthUser auth_user) {
        this.auth_user = auth_user;
    }

    public AuthUser getAuthUser() {
        return auth_user;
    }

    public void setAuth_user(AuthUser auth_user) {
        this.auth_user = auth_user;
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "auth_user=" + auth_user +
                '}';
    }
}
