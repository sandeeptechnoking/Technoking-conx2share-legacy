package com.conx2share.conx2share.model;


import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChatsHolder {

    private ArrayList<Chat> chats;

    @Nullable
    private Meta meta;

    public ChatsHolder(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public class Meta{
        @SerializedName("current_page")
        public Integer currentPage;

        @SerializedName("next_page")
        public Integer nextPage;

        @SerializedName("prev_page")
        public Integer prevPage;

        @SerializedName("total_pages")
        public Integer totalPages;

        @SerializedName("total_count")
        public Integer totalCount;
    }
}
