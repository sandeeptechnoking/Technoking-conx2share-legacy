package com.conx2share.conx2share.network.models.response;

import java.util.ArrayList;

public class HashTagsResponse {

    private ArrayList<HashTag> hashtags;

    public HashTagsResponse(ArrayList<HashTag> hashtags) {
        this.hashtags = hashtags;
    }

    public ArrayList<HashTag> getHashTags() {
        return hashtags;
    }

    public void setHashTags(ArrayList<HashTag> hashtags) {
        this.hashtags = hashtags;
    }
}
