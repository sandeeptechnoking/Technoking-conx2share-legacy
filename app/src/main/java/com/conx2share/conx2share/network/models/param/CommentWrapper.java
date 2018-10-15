package com.conx2share.conx2share.network.models.param;

import com.conx2share.conx2share.model.Comment;

public class CommentWrapper {

    public static final String TAG = CommentWrapper.class.getSimpleName();

    private Comment comment;

    public CommentWrapper(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
