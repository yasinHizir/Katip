package com.crypto.katip.controllers;

import androidx.annotation.Nullable;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.User;

public class UserController {
    private DbHelper dbHelper;

    public UserController(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void save(String username, String password) {
        User user = new User(username, password, dbHelper);
        user.save();
    }

    public boolean isRegistered(String username, String password) {
        User user = User.getInstance(username, dbHelper);
        if (user != null) {
            user.setPassword(password);
            return user.isRegistered();
        }
        return false;
    }

    public void updatePassword(int id, String password) {
        User user = User.getInstance(id, dbHelper);
        if (user != null) {
            user.setPassword(password);
            user.update();
        }
    }

    public void updateUsername(int id, String username) {
        User user = User.getInstance(id, dbHelper);
        if (user != null) {
            user.setUsername(username);
            user.update();
        }
    }

    public void remove(int id) {
        User user = User.getInstance(id, dbHelper);
        if (user != null) {
            user.remove();
        }
    }

    @Nullable
    public User getUser(String username) {
        return User.getInstance(username, dbHelper);
    }
}
