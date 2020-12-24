package com.crypto.katip.database.models;

import com.crypto.katip.cryptography.SignalStore;

public class User {
    private final int id;
    private final String username;
    private final SignalStore store;

    public User(int id, String username, SignalStore store) {
        this.id = id;
        this.username = username;
        this.store = store;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public SignalStore getStore() {
        return store;
    }
}
