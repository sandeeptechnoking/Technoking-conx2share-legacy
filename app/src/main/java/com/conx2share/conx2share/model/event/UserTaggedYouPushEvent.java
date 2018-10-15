package com.conx2share.conx2share.model.event;

public class UserTaggedYouPushEvent extends PushEvent {

    public UserTaggedYouPushEvent(String objectId, String messageType, String alertText) {
        super(objectId, messageType, alertText);
    }

}
