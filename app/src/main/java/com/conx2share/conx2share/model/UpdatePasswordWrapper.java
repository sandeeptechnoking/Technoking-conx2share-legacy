package com.conx2share.conx2share.model;

public class UpdatePasswordWrapper {

    public String authToken;
    public User user;
    public Integer id;

    public UpdatePasswordWrapper(String authToken, String newPass, Integer id) {
        this.authToken = authToken;
        this.user = new User(newPass);
        this.id = id;
    }

    public class User {

        public String password;
        public String passwordConfirmation;

        public User(String password) {
            this.password = password;
            this.passwordConfirmation = password;
        }
    }
}
