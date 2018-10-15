package com.conx2share.conx2share.network;

import com.conx2share.conx2share.network.models.UserTag;

import java.util.ArrayList;

public class PostObject {

    public static final String TAG = PostObject.class.getSimpleName();

    private String body;
    private ArrayList<UserTag> userTagsAttributes;

    public PostObject(String body, ArrayList<UserTag> userTags) {
        this.body = body;
        this.userTagsAttributes = userTags;
    }
}
