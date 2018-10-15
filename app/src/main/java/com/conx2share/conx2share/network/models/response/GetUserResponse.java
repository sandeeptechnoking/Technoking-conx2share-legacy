package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.network.models.User;

public class GetUserResponse {

    private User user;

    public GetUserResponse(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
