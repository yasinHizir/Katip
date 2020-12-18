package com.crypto.katip.communication;

import java.io.Serializable;

public class Envelope implements Serializable {
    private final String username;
    private final String body;

    public Envelope(String username, String body) {
        this.username = username;
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public String getBody() {
        return body;
    }
}
