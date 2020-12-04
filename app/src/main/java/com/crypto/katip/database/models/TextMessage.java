package com.crypto.katip.database.models;

public class TextMessage {
    private final int id;
    private final int chatId;
    private final boolean self;
    private final String body;

    public TextMessage(int id, boolean self,int chatId, String body) {
        this.id = id;
        this.chatId = chatId;
        this.self = self;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public int getChatId() {
        return chatId;
    }

    public boolean isSelf() {
        return self;
    }

    public String getBody() {
        return this.body;
    }
}
