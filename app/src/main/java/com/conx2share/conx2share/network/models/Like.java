package com.conx2share.conx2share.network.models;

public class Like {

    private int likeeId;

    private int likerId;

    private int id;

    public Like(int likeeId) {
        this.likeeId = likeeId;
    }

    public Like(int likeeId, int likerId, int id) {
        this.likeeId = likeeId;
        this.likerId = likerId;
        this.id = id;
    }

    public int getLikeeId() {
        return likeeId;
    }

    public void setLikeeId(int likeeId) {
        this.likeeId = likeeId;
    }

    public int getLikerId() {
        return likerId;
    }

    public void setLikerId(int likerId) {
        this.likerId = likerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
