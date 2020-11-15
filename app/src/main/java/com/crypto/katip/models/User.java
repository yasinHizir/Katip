package com.crypto.katip.models;


import androidx.annotation.Nullable;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;

public class User {
    private int id;
    private String username;
    private String password;
    private final UserDatabase database;

    public User(int id, String username, DbHelper dbHelper) {
        this.id = id;
        this.username = username;
        this.database = new UserDatabase(dbHelper);
    }

    public User(String username, String password, DbHelper dbHelper) {
        this.username = username;
        this.password = password;
        this.database = new UserDatabase(dbHelper);
    }

    public void save() {
        database.save(this.username, this.password);
    }

    public void update() {
        database.update(this.id, this.username, this.password);
    }

    public void remove() {
        database.remove(this.id);
    }

    public boolean isRegistered() {
        return database.isRegistered(this.username, this.password);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public static User getInstance(int id, DbHelper dbHelper) {
        UserDatabase userDatabase = new UserDatabase(dbHelper);
        return userDatabase.getUser(id);
    }

    @Nullable
    public static User getInstance(String username, DbHelper dbHelper) {
        UserDatabase userDatabase = new UserDatabase(dbHelper);
        return userDatabase.getUser(username);
    }
}
