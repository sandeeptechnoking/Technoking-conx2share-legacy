package com.conx2share.conx2share.model;


import java.util.ArrayList;

public class ExpirationWrapper {

    private ArrayList<String> ids;

    private Message message;

    public ExpirationWrapper(ArrayList<String> ids, Message message) {
        this.ids = ids;
        this.message = message;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
