package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Message;

public class SendMessageResponse {

    private Message message;

    public SendMessageResponse(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
