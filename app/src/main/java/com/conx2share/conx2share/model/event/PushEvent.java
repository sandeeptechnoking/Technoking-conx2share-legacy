package com.conx2share.conx2share.model.event;

public abstract class PushEvent {

    private String mObjectId;

    private String mMessageType;

    private String mAlertText;

    public PushEvent(String objectId, String messageType, String alertText) {
        mObjectId = objectId;
        mMessageType = messageType;
        mAlertText = alertText;
    }

    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(String objectId) {
        mObjectId = objectId;
    }

    public String getMessageType() {
        return mMessageType;
    }

    public void setMessageType(String messageType) {
        mMessageType = messageType;
    }

    public String getAlertText() {
        return mAlertText;
    }

    public void setAlertText(String alertText) {
        mAlertText = alertText;
    }
}
