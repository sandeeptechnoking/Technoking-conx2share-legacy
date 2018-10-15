package com.conx2share.conx2share.model;

/**
 * Created by heathersnepenger on 3/7/17.
 */

public class UpdateResponse {

    private Boolean update_required;
    private String title;
    private String message;

    public Boolean getUpdate_required() {
        return update_required;
    }

    public String getTitle() {
        if (title == null) {
            return "Update required!";
        }
        return title;
    }

    public String getMessage() {
        if (message == null) {
            return "Please update the app now!";
        }
        return message;
    }
}
