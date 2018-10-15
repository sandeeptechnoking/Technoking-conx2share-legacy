package com.conx2share.conx2share.model;

import java.util.Date;

public class ApiUser {

    private String email;

    private String password;

    private DeviceAttributes device_attributes;

    private String firstName;

    private String lastName;

    private String passwordConfirmation;

    private String username;

    private String locale;

    private String birthday;

    public ApiUser(String locale, String email, String password, DeviceAttributes device_attributes, String firstName, String lastName, String passwordConfirmation, String username) {
        this.locale = locale;
        this.email = email;
        this.password = password;
        this.device_attributes = device_attributes;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordConfirmation = passwordConfirmation;
        this.username = username;
    }

    public ApiUser(String email, String password, DeviceAttributes device_attributes) {
        this.email = email;
        this.password = password;
        this.device_attributes = device_attributes;
    }

    public ApiUser(String email, String password, DeviceAttributes device_attributes, String locale) {
        this.locale = locale;
        this.email = email;
        this.password = password;
        this.device_attributes = device_attributes;
    }

    public ApiUser(String email, String password, String firstName, String lastName, String passwordConfirmation, String username, String locale) {
        this.locale = locale;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordConfirmation = passwordConfirmation;
        this.username = username;
    }

    public ApiUser(String email, String password, String firstName, String lastName, String passwordConfirmation, String username) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordConfirmation = passwordConfirmation;
        this.username = username;
    }


    public ApiUser(String email, String password, DeviceAttributes device_attributes, String firstName, String lastName, String passwordConfirmation, String username, String locale) {
        this.locale = locale;
        this.email = email;
        this.password = password;
        this.device_attributes = device_attributes;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordConfirmation = passwordConfirmation;
        this.username = username;
    }

    public ApiUser(String email, String password, DeviceAttributes device_attributes, String firstName, String lastName, String passwordConfirmation, String username) {
        this.email = email;
        this.password = password;
        this.device_attributes = device_attributes;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordConfirmation = passwordConfirmation;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DeviceAttributes getDevice_attributes() {
        return device_attributes;
    }

    public void setDevice_attributes(DeviceAttributes device_attributes) {
        this.device_attributes = device_attributes;
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

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}