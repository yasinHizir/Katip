package com.crypto.katip.ui.register;

import com.crypto.katip.models.LoggedInUser;

public class RegisterResult {
    private LoggedInUser success;
    private String error;

    public RegisterResult(LoggedInUser user) {
        this.success = user;
    }

    public RegisterResult(String error) {
        this.error = error;
    }


    public LoggedInUser getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
