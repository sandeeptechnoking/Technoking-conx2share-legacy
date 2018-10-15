package com.conx2share.conx2share.model;

import com.conx2share.conx2share.network.models.User;

import java.util.ArrayList;

public class Users {

    private ArrayList<User> users;

    public Users(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
