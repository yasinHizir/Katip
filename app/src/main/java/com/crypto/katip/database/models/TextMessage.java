package com.crypto.katip.database.models;

public class TextMessage {
    private final int id;
    private final int chatId;
    private final boolean own;
    private final String body;

    public TextMessage(int id, boolean own, int chatId, String body) {
        this.id = id;
        this.chatId = chatId;
        this.own = own;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public int getChatId() {
        return chatId;
    }

    public boolean isOwn() {
        return own;
    }

    public String getBody() {
        return this.body;
    }
}
