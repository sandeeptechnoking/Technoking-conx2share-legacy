package com.conx2share.conx2share.network.models;

public class MessageWrapper {

    private SendMessage message;

    private String chatId;

    private Boolean xmpp = true;

    private String auth_token;

    public MessageWrapper(SendMessage message, String auth_token) {
        this.message = message;
        this.auth_token = auth_token;
    }

    public SendMessage getMessage() {
        return message;
    }

    public void setMessage(SendMessage message) {
        this.message = message;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Boolean getXmpp() {
        return xmpp;
    }

    public void setXmpp(Boolean xmpp) {
        this.xmpp = xmpp;
    }

}
