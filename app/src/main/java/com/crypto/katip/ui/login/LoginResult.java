package com.crypto.katip.ui.login;

/**
 * This class represents login result.
 */
public class LoginResult {
    private boolean success = false;
    private int error;

    public LoginResult(boolean success) {
        this.success = success;
    }

    public LoginResult(int error) {
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }

    public int getError() {
        return error;
    }
}