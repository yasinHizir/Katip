package com.crypto.katip.communication;

import java.io.Serializable;

public class Envelope implements Serializable {
    private final String username;
    private final byte[] body;

    public Envelope(String username, byte[] body) {
        this.username = username;
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getBody() {
        return body;
    }
}