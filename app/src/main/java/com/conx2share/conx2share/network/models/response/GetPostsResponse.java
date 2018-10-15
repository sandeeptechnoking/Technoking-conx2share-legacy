package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Post;

import java.util.ArrayList;

public class GetPostsResponse {

    private ArrayList<Post> posts;

    private Post post;

    private Meta meta;

    public GetPostsResponse(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public GetPostsResponse(Post post) {
        this.post = post;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Meta getMeta() {
        return meta;
    }

    public class Meta {
        public String currentPage;
        public Integer totalPages;
        public Integer totalCount;
        public Page page;
    }

    public class Page {
        public String number;
        public Integer size;
        public Integer totalCount;
    }
}
