package com.conx2share.conx2share.model.event;

import com.conx2share.conx2share.model.Message;

public class PushNotificationEvent {

    public Message mMessage;

    public boolean fromGCM;

    public PushNotificationEvent(Message message, boolean fromGCM) {
        mMessage = message;
        this.fromGCM = fromGCM;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message message) {
        mMessage = message;
    }

    public boolean isFromGCM() {
        return fromGCM;
    }

    public void setFromGCM(boolean fromGCM) {
        this.fromGCM = fromGCM;
    }
}
