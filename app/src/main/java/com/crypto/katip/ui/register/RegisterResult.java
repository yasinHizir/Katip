package com.crypto.katip.ui.register;

import com.crypto.katip.models.User;

public class RegisterResult {
    private User success;
    private String error;

    public RegisterResult(User success) {
        this.success = success;
    }

    public RegisterResult(String error) {
        this.error = error;
    }

    public User getSuccess() {
        return this.success;
    }

    public String getError() {
        return this.error;
    }

}
