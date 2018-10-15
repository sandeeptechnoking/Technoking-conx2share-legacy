package com.conx2share.conx2share.model.event;

public class UpdateProfileImageEvent {

    private String imageUrl;

    public UpdateProfileImageEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
