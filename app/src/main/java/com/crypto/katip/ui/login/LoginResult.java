package com.crypto.katip.ui.login;

import com.crypto.katip.database.models.User;

public class LoginResult {
    private User success;
    private int error;

    public LoginResult(User user) {
        this.success = user;
    }

    public LoginResult(int error) {
        this.error = error;
    }

    public User getSuccess() {
        return success;
    }

    public int getError() {
        return error;
    }
}
