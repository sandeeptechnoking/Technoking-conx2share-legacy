package com.conx2share.conx2share.network.models.param;

public class GetHashTagFeedParams {

    private String title;

    private Integer postId;

    private String direction;

    public GetHashTagFeedParams(String title) {
        this.title = title;
    }

    public GetHashTagFeedParams(String title, int postId, String direction) {
        this.title = title;
        this.postId = postId;
        this.direction = direction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPostId() {
        return postId;

    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
