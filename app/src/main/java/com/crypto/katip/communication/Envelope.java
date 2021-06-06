package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class Envelope implements Serializable {
    public static final int START_CHAT_TYPE = 0;
    public static final int TEXT_TYPE = 1;

    private final int type;
    private final String uuid;
    private final String username;
    private final byte[] message;

    public Envelope(int type, UUID uuid, String username, byte[] message) {
        this.type = type;
        this.uuid = uuid.toString();
        this.username = username;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public String getUsername() {
        return username;
    }

    public byte[] getMessage() {
        return message;
    }

    @Nullable
    public static byte[] serialize(Envelope envelope) {
        byte[] bytes = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            ObjectOutputStream objectOutputStream =  new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(envelope);
            objectOutputStream.close();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    @Nullable
    public static Envelope deserialize(byte[] bytes) {
        Envelope envelope = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            envelope = (Envelope) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return envelope;
    }
}