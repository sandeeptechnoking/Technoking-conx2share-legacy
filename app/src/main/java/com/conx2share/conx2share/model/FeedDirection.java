package com.conx2share.conx2share.model;

/**
 * Encapsulates the direction parameter that some of the server's endpoints expect,
 * i.e. - /api/users/get_feed, /api/posts/get_feed
 */
public enum FeedDirection {

    NEWER("Newer"),
    OLDER("Older");

    private String mValue;

    FeedDirection(String value) {
        this.mValue = value;
    }

    @Override
    public String toString() {
        return mValue;
    }

}
