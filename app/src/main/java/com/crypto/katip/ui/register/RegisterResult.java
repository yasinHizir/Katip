package com.crypto.katip.ui.register;


public class RegisterResult {
    private String username;
    private String password;
    private String error;

    public RegisterResult(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RegisterResult(String error) {
        this.error = error;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    public String getError() {
        return this.error;
    }

}
