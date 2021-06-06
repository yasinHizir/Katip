 package com.crypto.katip.database.models;

import com.crypto.katip.cryptography.SignalStore;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private final int id;
    private final String username;
    private final SignalStore store;

    public User(UUID uuid, int id, String username, SignalStore store) {
        this.uuid = uuid;
        this.id = id;
        this.username = username;
        this.store = store;
    }

    public UUID getUuid() {
        return uuid;
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
