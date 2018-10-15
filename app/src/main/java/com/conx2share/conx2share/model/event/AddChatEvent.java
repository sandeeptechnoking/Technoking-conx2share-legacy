package com.conx2share.conx2share.model.event;


import android.app.Activity;

public class AddChatEvent {

    Integer chatId;

    Activity mActivity;

    public AddChatEvent(Integer chatId, Activity activity) {
        this.chatId = chatId;
        mActivity = activity;
    }

    public Integer getChatId() {
        return chatId;
    }

    public Activity getActivity() {
        return mActivity;
    }
}
