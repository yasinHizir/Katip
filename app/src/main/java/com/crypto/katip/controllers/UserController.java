package com.crypto.katip.controllers;

import androidx.annotation.Nullable;

import com.crypto.katip.models.User;

public class UserController {
    public void save(String username, String password) {
        User user = new User(username, password);
        user.save();
    }

    public boolean isRegistered(String username, String password) {
        User user = User.getInstance(username);
        if (user != null) {
            return user.isRegistered();
        }
        return false;
    }

    public void updatePassword(int id, String password) {
        User user = User.getInstance(id);
        if (user != null) {
            user.setPassword(password);
            user.update();
        }
    }

    public void updateUsername(int id, String username) {
        User user = User.getInstance(id);
        if (user != null) {
            user.setUsername(username);
            user.update();
        }
    }

    public void remove(int id) {
        User user = User.getInstance(id);
        if (user != null) {
            user.remove();
        }
    }

    @Nullable
    public User getUser(String username) {
        return User.getInstance(username);
    }
}
