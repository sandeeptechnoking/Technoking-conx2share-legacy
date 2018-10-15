package com.conx2share.conx2share.model;

public class PushNotification {

    private String object_id;

    private String message_type;

    private String alert_text;

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getAlert_text() {
        return alert_text;
    }

    public void setAlert_text(String alert_text) {
        this.alert_text = alert_text;
    }
}
