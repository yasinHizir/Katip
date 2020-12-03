package com.crypto.katip.models;

import com.crypto.katip.database.MessageDatabase;

public class TextMessage {
    private int id;
    private int chatId;
    private boolean self;
    private String body;
    private MessageDatabase database;

    public TextMessage(int chatId, boolean self, String text, MessageDatabase database) {
        this.chatId = chatId;
        this.self = self;
        this.body = text;
        this.database = database;
    }

    public TextMessage(int id, boolean self,int chatId, String body, MessageDatabase database) {
        this.id = id;
        this.chatId = chatId;
        this.self = self;
        this.body = body;
        this.database = database;
    }

    public boolean isSelf() {
        return self;
    }

    public String getBody() {
        return this.body;
    }

    public void save() {
        database.save(this.body, this.self);
    }

    public void remove() {
        database.remove(this.id);
    }
}
