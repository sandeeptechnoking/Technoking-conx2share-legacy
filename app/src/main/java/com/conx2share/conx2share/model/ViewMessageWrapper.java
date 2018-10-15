package com.conx2share.conx2share.model;

public class ViewMessageWrapper {

    private ViewMessage message;

    private String auth_token;

    private String id;

    public ViewMessageWrapper(ViewMessage message, String auth_token, String id) {
        this.message = message;
        this.auth_token = auth_token;
        this.id = id;
    }

    public ViewMessage getMessage() {
        return message;
    }

    public void setMessage(ViewMessage message) {
        this.message = message;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
