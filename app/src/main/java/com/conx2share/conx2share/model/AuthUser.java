package com.conx2share.conx2share.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.conx2share.conx2share.network.models.User;

import java.lang.reflect.Type;

public class AuthUser {

    private Integer id;

    private String birthday;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String authenticationToken;

    private UserAvatar avatar;

    private String plan;

    private boolean promoUser;

    private Boolean overEighteen;

    // TODO: this constructor is used only in tests.  eliminate it.  other developers may use it in live code which is error prone as there are no null checks on fields, e.g.-id.
    public AuthUser(Integer id, String birthday, String firstName, String lastName, String username, String email, String authenticationToken, UserAvatar avatar, String plan) {
        this.id = id;
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.authenticationToken = authenticationToken;
        this.avatar = avatar;
        this.plan = plan;
    }

    public AuthUser(int id) {
        this.id = id;
    }

    public static AuthUser fromJsonString(String jsonString) {

        Gson gson = new GsonBuilder().create();
        Type authUserType = new TypeToken<AuthUser>() {
        }.getType();
        return gson.fromJson(jsonString, authUserType);
    }

    public void updateFromUser(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
    }

    public boolean isValid() {
        if (getId() == null) {
            return false;
        } else if (getPlan() == null) {
            return false;
        } else if (getAuthenticationToken() == null) {
            return false;
        }

        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(UserAvatar avatar) {
        this.avatar = avatar;
    }

    public String toJsonString() {

        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public boolean isPromoUser() {
        return promoUser;
    }

    public void setPromoUser(boolean promoUser) {
        this.promoUser = promoUser;
    }


    public Boolean isOverEighteen() {
        return overEighteen;
    }

    public void setOverEighteen(Boolean overEighteen) {
        this.overEighteen = overEighteen;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "id=" + id +
                ", birthday='" + birthday + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", authenticationToken='" + authenticationToken + '\'' +
                ", avatar=" + avatar + '\'' +
                ", plan=" + plan +
                '}';
    }
}
