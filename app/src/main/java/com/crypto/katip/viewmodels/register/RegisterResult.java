package com.crypto.katip.viewmodels.register;

import com.crypto.katip.models.LoggedInUser;
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
        return success;
    }

}
