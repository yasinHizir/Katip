package com.crypto.katip.controllers;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.LoggedInUser;

public class LoginController {
    private static volatile LoginController instance;
    private LoggedInUser user;

    public static LoginController getInstance(){
        if (instance == null){
            instance = new LoginController();
        }
        return instance;
    }

    public void login(String username, String password, DbHelper dbHelper) {
        UserController userController = new UserController(dbHelper);
        if (userController.isRegistered(username, password)) {
            this.user = new LoggedInUser(userController.getUser(username).getId(), username);
        }
    }

    public void logout() {
        this.user = null;
    }

    public LoggedInUser getUser(){
        return user;
    }
}
