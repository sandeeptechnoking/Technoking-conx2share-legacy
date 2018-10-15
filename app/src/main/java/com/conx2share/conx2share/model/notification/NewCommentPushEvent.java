package com.conx2share.conx2share.model.notification;

import com.conx2share.conx2share.model.event.PushEvent;

public class NewCommentPushEvent extends PushEvent {

    public NewCommentPushEvent(String objectId, String messageType, String alertText) {
        super(objectId, messageType, alertText);
    }
}
