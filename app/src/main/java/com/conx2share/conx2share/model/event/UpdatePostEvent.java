package com.conx2share.conx2share.model.event;

import com.conx2share.conx2share.model.Post;

public class UpdatePostEvent {

    private int position;

    private PostEventType updateType;

    private Post post;

    public UpdatePostEvent(int position, PostEventType updateType) {
        this.position = position;
        this.updateType = updateType;
    }

    public UpdatePostEvent(Post post) {
        this.post = post;
        this.updateType = PostEventType.POST_BODY_WAS_CHANGED;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PostEventType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(PostEventType updateType) {
        this.updateType = updateType;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public enum PostEventType {
        COMMENT_COUNT,
        LIKE_COUNT_INCREASE,
        LIKE_COUNT_DECREASE,
        POST_BODY_WAS_CHANGED
    }
}
