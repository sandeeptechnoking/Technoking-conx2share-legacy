package com.conx2share.conx2share.model.event;

import com.conx2share.conx2share.model.Post;

public class PostCreatedEvent {
    public final Post post;

    public PostCreatedEvent(Post post) {
        this.post = post;
    }
}