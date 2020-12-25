package com.crypto.katip.communication;

import java.io.Serializable;
import java.util.UUID;

public class Envelope implements Serializable {
    private final int type;
    private final String uuid;
    private final String username;
    private final int deviceId;
    private final byte[] body;

    public Envelope(int type, UUID uuid, String username, int deviceId, byte[] body) {
        this.type = type;
        this.uuid = uuid.toString();
        this.username = username;
        this.deviceId = deviceId;
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public byte[] getBody() {
        return body;
    }
}