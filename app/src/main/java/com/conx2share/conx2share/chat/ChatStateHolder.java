package com.conx2share.conx2share.chat;


public class ChatStateHolder {

    private static volatile ChatStateHolder instance;

    public static ChatStateHolder getInstance() {
        ChatStateHolder localInstance = instance;
        if (localInstance == null) {
            synchronized (ChatStateHolder.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ChatStateHolder();
                }
            }
        }

        return localInstance;
    }

    private ChatStateHolder(){}

    private boolean active;
    private int userId;

    public boolean isChatActive() {
        return active;
    }

    public void setChatActive(boolean chatActive, int userId) {
        synchronized (ChatStateHolder.class) {
            this.active = chatActive;
            this.userId = chatActive ? userId : 0;
        }
    }

    public int getUserId() {
        return userId;
    }

}
