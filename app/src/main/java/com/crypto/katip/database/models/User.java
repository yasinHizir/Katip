package com.crypto.katip.database.models;

import com.crypto.katip.cryptography.SignalProtocolStore;

public class User {
    private final int id;
    private final String username;
    private final SignalProtocolStore store;

    public User(int id, String username, SignalProtocolStore store) {
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

    public SignalProtocolStore getStore() {
        return store;
    }
}
