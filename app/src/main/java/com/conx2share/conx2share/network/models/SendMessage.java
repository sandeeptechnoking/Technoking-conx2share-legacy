package com.conx2share.conx2share.network.models;

import com.conx2share.conx2share.model.Image;
import com.conx2share.conx2share.model.Video;

public class SendMessage {

    private String body;

    private String textColor;

    private Image image;

    private String title;

    private Integer toId;

    private Video video;

    private Integer chatId;

    private Integer timeToLive;

    private Integer audioLength;

    public SendMessage(String body, String textColor, String title, Integer toId) {
        this.body = body;
        this.textColor = textColor;
        this.title = title;
        this.toId = toId;
    }

    public SendMessage(String body, String textColor, Image image, Integer toId) {
        this.body = body;
        this.textColor = textColor;
        this.image = image;
        this.toId = toId;
    }

    public SendMessage(String body, String textColor, Integer toId, Video video) {
        this.body = body;
        this.textColor = textColor;
        this.toId = toId;
        this.video = video;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer to_id) {
        this.toId = to_id;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Integer getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(Integer audioLength) {
        this.audioLength = audioLength;
    }
}
