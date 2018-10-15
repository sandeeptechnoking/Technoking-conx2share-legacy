package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.models.Like;

public class LikeUnlikeHelper {

    public boolean liking;

    public Like like;

    public Post post;

    public LikeUnlikeHelper(boolean liking, Like like, Post post) {
        this.liking = liking;
        this.like = like;
        this.post = post;
    }
}