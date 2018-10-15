package com.conx2share.conx2share.network.models;

public class PhotoMessage {

    private String body;

    private String text_color;

    private String title;

    private String image;

    private String to_id;

    private String video;

    public PhotoMessage(String body, String text_color, String title, String image, String to_id, String video) {
        this.body = body;
        this.text_color = text_color;
        this.title = title;
        this.image = image;
        this.to_id = to_id;
        this.video = video;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getText_color() {
        return text_color;
    }

    public void setText_color(String text_color) {
        this.text_color = text_color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
