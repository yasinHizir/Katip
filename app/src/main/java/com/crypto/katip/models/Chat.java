package com.crypto.katip.models;

import androidx.annotation.Nullable;

import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;

import java.util.ArrayList;

public class Chat {
    private int id;
    private final int userId;
    private String interlocutor;
    private ChatDatabase database;

    public Chat(int userId, String interlocutor, ChatDatabase database) {
        this.userId = userId;
        this.interlocutor = interlocutor;
        this.database = database;
    }

    public Chat(int id, int userId, String interlocutor){
        this.id = id;
        this.userId = userId;
        this.interlocutor = interlocutor;
    }

    public void save() {
        database.save(this.interlocutor);
    }

    public void remove() {
        database.remove(this.id);
    }

    public static ArrayList<String> getChatNames(DbHelper dbHelper, int userId) {
        ChatDatabase database = new ChatDatabase(dbHelper, userId);
        return database.getChatNames();
    }

    @Nullable
    public static Chat getInstance(DbHelper dbHelper, int userId, int chatId) {
        ChatDatabase database = new ChatDatabase(dbHelper, userId);
        return database.getChat(chatId);
    }

    @Nullable
    public static Chat getInstance(DbHelper dbHelper, int userId, String interlocutor) {
        ChatDatabase database = new ChatDatabase(dbHelper, userId);
        return database.getChat(interlocutor);
    }

    public int getUserId() {
        return this.userId;
    }
}