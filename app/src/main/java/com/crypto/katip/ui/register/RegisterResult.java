package com.crypto.katip.ui.register;

public class RegisterResult {
    private String username;
    private String password;
    private int error;

    public RegisterResult(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RegisterResult(int error) {
        this.error = error;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    public int getError() {
        return this.error;
    }
}
