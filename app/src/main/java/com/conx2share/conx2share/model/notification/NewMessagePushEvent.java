package com.conx2share.conx2share.model.notification;

import com.conx2share.conx2share.model.event.PushEvent;

public class NewMessagePushEvent extends PushEvent {

    public NewMessagePushEvent(String objectId, String messageType, String alertText) {
        super(objectId, messageType, alertText);
    }
}
