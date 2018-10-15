package com.conx2share.conx2share.network.models.response;

public class NotificationsUpdateViewedResponse {

    private int badgecount;

    public NotificationsUpdateViewedResponse(int badgecount) {

        this.badgecount = badgecount;
    }

    public int getBadgecount() {
        return badgecount;
    }

    public void setBadgecount(int badgecount) {
        this.badgecount = badgecount;
    }
}
