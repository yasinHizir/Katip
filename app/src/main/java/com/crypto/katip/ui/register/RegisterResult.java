package com.crypto.katip.ui.register;

/**
 * This class represents register result.
 */
public class RegisterResult {
    private boolean success = false;
    private int error;

    public RegisterResult(boolean success) {
        this.success = success;
    }

    public RegisterResult(int error) {
        this.error = error;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public int getError() {
        return this.error;
    }
}
