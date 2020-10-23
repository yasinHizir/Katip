package com.crypto.katip.model;

import android.content.Context;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;

public class User {
    private int id;
    private String userName;
    private UserDatabase userDatabase;

    public User(int id, String userName, Context context){
        this.id = id;
        this.userName = userName;
        this.userDatabase = new UserDatabase(new DbHelper(context));
    }

    public boolean save(String password){
        userDatabase.save(userName, password);
        return true;
    }

    public String getUserName() {
        return userName;
    }

    public int getId() {
        return id;
    }
}
