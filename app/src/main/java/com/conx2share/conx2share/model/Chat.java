package com.conx2share.conx2share.model;

import com.conx2share.conx2share.network.models.User;

import java.util.ArrayList;

public class Chat {

    private int id;

    private ArrayList<User> users;

    private String createdAt;

    private String updatedAt;

    private int missedMessages;

    private Message latestMessage;

    public int getId() {
        return id;
    }

    public int getMissedMessages() {
        return missedMessages;
    }

    public void setMissedMessages(int missedMessages) {
        this.missedMessages = missedMessages;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }
}
