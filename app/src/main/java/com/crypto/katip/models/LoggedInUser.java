package com.crypto.katip.models;

public class LoggedInUser {
    private int id;
    private String username;

    public LoggedInUser(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
