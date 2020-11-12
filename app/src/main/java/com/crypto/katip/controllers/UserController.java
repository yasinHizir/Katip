package com.crypto.katip.controllers;

import com.crypto.katip.models.User;

public class UserController {
    public User save(String username, String password) {
        User user = new User();
        user.setId(0);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
