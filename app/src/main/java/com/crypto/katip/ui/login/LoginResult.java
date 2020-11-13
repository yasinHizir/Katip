package com.crypto.katip.ui.login;

import com.crypto.katip.models.LoggedInUser;

public class LoginResult {
    private LoggedInUser success;
    private String error;

    public LoginResult(LoggedInUser user) {
        this.success = user;
    }

    public LoginResult(String error) {
        this.error = error;
    }

    public LoggedInUser getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
