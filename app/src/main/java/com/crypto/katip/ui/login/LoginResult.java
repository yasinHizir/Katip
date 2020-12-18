package com.crypto.katip.ui.login;

import com.crypto.katip.database.models.User;

public class LoginResult {
    private User success;
    private String error;

    public LoginResult(User user) {
        this.success = user;
    }

    public LoginResult(String error) {
        this.error = error;
    }

    public User getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
