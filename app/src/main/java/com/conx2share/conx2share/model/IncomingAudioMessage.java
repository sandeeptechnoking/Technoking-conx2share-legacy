package com.conx2share.conx2share.model;

public class IncomingAudioMessage extends Message {

    public IncomingAudioMessage(Message message) {
        this.id = message.id;
        this.body = message.body;
        this.video = message.video;
        this.image = message.image;
        this.audio = message.audio;
        this.userId = message.userId;
        this.toId = message.toId;
        this.created_at = message.created_at;
        this.message_type = message.message_type;
        this.streamingUrl = message.streamingUrl;
        this.chatId = message.chatId;
        this.expirationTime = message.expirationTime;
        this.timeToLive = message.timeToLive;
        this.displayTime = message.displayTime;
        this.userFirstName = message.userFirstName;
        this.userLastName = message.userLastName;
        this.userAvatar = message.userAvatar;
        this.audioLength = message.audioLength;
        this.userUsername = message.userUsername;
    }
}
