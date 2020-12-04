package com.crypto.katip.database.models;

public class Chat {
    private final int id;
    private final int userId;
    private final String interlocutor;

    public Chat(int id, int userId, String interlocutor){
        this.id = id;
        this.userId = userId;
        this.interlocutor = interlocutor;
    }

    public int getId() {
        return this.id;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getInterlocutor() {
        return this.interlocutor;
    }
}