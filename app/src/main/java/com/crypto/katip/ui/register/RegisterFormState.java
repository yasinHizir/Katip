package com.crypto.katip.ui.register;

/**
 * This class represents errors in registration form
 */
public class RegisterFormState {
    private int usernameError;
    private int passwordError;
    private int passwordVerifyError;
    private boolean isDataValid;

    public RegisterFormState(int usernameError, int passwordError, int passwordVerifyError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.passwordVerifyError = passwordVerifyError;
    }

    public RegisterFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }

    public int getUsernameError() {
        return usernameError;
    }

    public int getPasswordError() {
        return passwordError;
    }

    public int getPasswordVerifyError() {
        return passwordVerifyError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
