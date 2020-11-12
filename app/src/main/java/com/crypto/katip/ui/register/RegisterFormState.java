package com.crypto.katip.ui.register;

public class RegisterFormState {
    private String usernameError;
    private String passwordError;
    private String passwordVerifyError;
    private boolean isDataValid;

    public RegisterFormState(String usernameError, String passwordError, String passwordVerifyError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.passwordVerifyError = passwordVerifyError;
    }

    public RegisterFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public String getPasswordVerifyError() {
        return passwordVerifyError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
