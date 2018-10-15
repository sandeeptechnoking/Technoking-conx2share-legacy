package com.conx2share.conx2share.network.models;

import java.util.ArrayList;

public class GetFriendsResponse {

    private ArrayList<User> users;

    public GetFriendsResponse(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
