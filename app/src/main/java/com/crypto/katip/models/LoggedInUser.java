package com.crypto.katip.models;

import java.io.Serializable;

public class LoggedInUser implements Serializable {
    private final int id;
    private final String username;

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
