package com.crypto.katip.models;

import android.content.Context;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;

public class User {
    private int id;
    private String username;
    private String password;

    private UserDatabase userDatabase;

    public User(Context context) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
        this.userDatabase = new UserDatabase(new DbHelper(context));
    }

    public void select(String username) {
        User temp_user = userDatabase.selectUser(username);
        this.id = temp_user.getId();
        this.username = temp_user.getUsername();
        this.password = temp_user.getPassword();
    }

    public boolean save() {
        userDatabase.createUser(username, password);
        return true;
    }

    public boolean isRegistered() {
        return userDatabase.isRegistered(this.username, this.password);
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
