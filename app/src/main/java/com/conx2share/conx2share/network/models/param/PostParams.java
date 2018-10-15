package com.conx2share.conx2share.network.models.param;

import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.ui.feed.post.PostReceiver;
import com.conx2share.conx2share.util.TypedUri;

import java.util.ArrayList;

import retrofit.mime.TypedFile;

public class PostParams {

    private String id;

    private String body;

    private TypedUri picture;

    private TypedFile video;
    private ArrayList<UserTag> userTags;
    private PostReceiver mPostReceiver;
    private Boolean isPrivate;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TypedUri getPicture() {
        return picture;
    }

    public void setPicture(TypedUri picture) {
        this.picture = picture;
    }

    public TypedFile getVideo() {
        return video;
    }

    public void setVideo(TypedFile video) {
        this.video = video;
    }

    public ArrayList<UserTag> getUserTags() {
        return userTags;
    }

    public void setUserTags(ArrayList<UserTag> userTags) {
        this.userTags = userTags;
    }

    public PostReceiver getPostReceiver() {
        return mPostReceiver;
    }

    public void setPostReceiver(PostReceiver postReceiver) {
        mPostReceiver = postReceiver;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
