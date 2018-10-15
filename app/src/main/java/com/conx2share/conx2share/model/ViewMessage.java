package com.conx2share.conx2share.model;

public class ViewMessage {

    private String body;

    private Boolean viewed;

    public ViewMessage(String body, Boolean viewed) {
        this.body = body;
        this.viewed = viewed;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }
}
