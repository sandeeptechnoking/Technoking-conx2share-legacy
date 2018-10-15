package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Comment;

import java.util.ArrayList;

public class GetPostCommentsResponse {

    private ArrayList<Comment> comments;

    private Comment comment;

    public GetPostCommentsResponse(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public GetPostCommentsResponse(Comment comment) {
        this.comment = comment;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
