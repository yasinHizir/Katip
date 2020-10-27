package com.crypto.katip.models;

import android.content.Context;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;

public class User {
    private int id;
    private String username;
    private String password;

    public static User getUser(String username, Context context) {
        return new UserDatabase(new DbHelper(context)).getUser(username);
    }

    public static boolean save(String username, String password, Context context) {
        return new UserDatabase(new DbHelper(context)).saveUser(username, password);
    }

    public static boolean isRegistered(String username, String password, Context context) {
        return new UserDatabase(new DbHelper(context)).isRegistered(username, password);
    }

    public String show() {
        return this.id + " " + this.username + " " + this.password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
