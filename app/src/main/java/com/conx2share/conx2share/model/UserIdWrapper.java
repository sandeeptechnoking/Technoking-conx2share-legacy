package com.conx2share.conx2share.model;

public class UserIdWrapper {

    private String userId;

    public UserIdWrapper(String userId) {
        this.userId = userId;
    }

    public UserIdWrapper(int userId) {
        this.userId = String.valueOf(userId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
