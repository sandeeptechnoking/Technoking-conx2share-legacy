package com.conx2share.conx2share.ui.feed.post;

public interface PostReceiver {

    String getName();

    PostReceiverType getType();

    Integer getReceiverId();

    enum PostReceiverType {
        BUSINESS,
        GROUP,
        EVERYONE,
        FOLLOWERS
    }
}
