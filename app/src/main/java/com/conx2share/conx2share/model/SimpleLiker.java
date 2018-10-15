package com.conx2share.conx2share.model;

import com.conx2share.conx2share.util.DateUtils;

import java.util.Date;

public class SimpleLiker {

    private Integer userId;
    private String username, time, avatarUrl, name, firstName, lastName;
    private Date createdAt;

    public SimpleLiker() {
    }

    public SimpleLiker(Like like){
        this.avatarUrl = like.getLiker().getUser().getAvatarUrl();
        this.username = "@".concat(like.getLiker().getUser().getUsername());
        this.name = like.getLiker().getUser().getFirstName().concat(" ")
                .concat(like.getLiker().getUser().getLastName());
        this.firstName = like.getLiker().getUser().getFirstName();
        this.lastName = like.getLiker().getUser().getLastName();
        this.time = DateUtils.getDateAsDayMonthYear(like.getCreated_at());
        this.createdAt = new Date(DateUtils.getTimeMillis(like.getCreated_at()));
        this.userId = like.getLiker().getUser().getId();
    }

    public SimpleLiker(Integer userId, String username, String time, String avatarUrl) {
        this.userId = userId;
        this.username = username;
        this.time = time;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTime() {
        return time;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
