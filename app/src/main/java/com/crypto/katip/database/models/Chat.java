package com.crypto.katip.database.models;

import java.util.UUID;

public class Chat {
    private final int id;
    private final int userId;
    private final UUID remoteUUID;
    private final String interlocutor;

    public Chat(int id, int userId, UUID remoteUUID, String interlocutor){
        this.id = id;
        this.userId = userId;
        this.remoteUUID = remoteUUID;
        this.interlocutor = interlocutor;
    }

    public int getId() {
        return this.id;
    }

    public int getUserId() {
        return this.userId;
    }

    public UUID getRemoteUUID() {
        return remoteUUID;
    }

    public String getInterlocutor() {
        return this.interlocutor;
    }
}