package com.crypto.katip.models;

import androidx.annotation.Nullable;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;

public class User {
    private int id;
    private String username;
    private String password;


    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Nullable
    public static User getInstance(int id) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper());
        return userDatabase.findUser(id);
    }

    @Nullable
    public static User getInstance(String username) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper());
        return userDatabase.selectUser(username);
    }

    public void save() {
        new UserDatabase(new DbHelper()).saveUser(this.username, this.password);
    }

    public void update() {
        new UserDatabase(new DbHelper()).saveUser(this.username, this.password);
    }

    public void remove() {
        new UserDatabase(new DbHelper()).removeUser(this.id);
    }

    public boolean isRegistered() {
        return new UserDatabase(new DbHelper()).isRegistered(this.username, this.password);
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


}
