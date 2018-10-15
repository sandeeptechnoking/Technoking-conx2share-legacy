package com.conx2share.conx2share.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsSources {

    @SerializedName("news_feeds")
    private ArrayList<NewsSource> newSources;

    public NewsSources(ArrayList<NewsSource> newSources) {
        this.newSources = newSources;
    }

    public ArrayList<NewsSource> getNewSources() {
        return newSources;
    }

    public void setNewSources(ArrayList<NewsSource> newSources) {
        this.newSources = newSources;
    }
}
