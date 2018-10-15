package com.conx2share.conx2share.network.models.response;

import android.text.TextUtils;

import com.conx2share.conx2share.model.IncomingAudioMessage;
import com.conx2share.conx2share.model.Message;

import java.util.ArrayList;

public class MessagesResponse {

    private ArrayList<Message> messages;

    public MessagesResponse(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
