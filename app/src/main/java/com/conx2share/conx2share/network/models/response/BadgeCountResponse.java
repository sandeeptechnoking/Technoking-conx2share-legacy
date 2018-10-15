package com.conx2share.conx2share.network.models.response;


public class BadgeCountResponse {

    private int badgeCount;
    private int unread_message_badge_count;
    private int unread_non_message_badge_count;

    public BadgeCountResponse(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public int getUnread_message_badge_count() {
        return unread_message_badge_count;
    }

    public int getUnread_non_message_badge_count() {
        return unread_non_message_badge_count;
    }
}
