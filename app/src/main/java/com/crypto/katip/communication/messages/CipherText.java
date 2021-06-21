package com.crypto.katip.communication.messages;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class represents ciphertext message sent to server.
 */
public class CipherText implements Serializable {
    private final int cipherType;       // CiphertextMessage.WHISPER_TYPE or CiphertextMessage.PREKEY_TYPE
    private final byte[] ciphertext;

    public CipherText(int cipherType, byte[] ciphertext) {
        this.cipherType = cipherType;
        this.ciphertext = ciphertext;
    }

    public int getCipherType() {
        return cipherType;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    @Nullable
    public static byte[] serialize(CipherText message) {
        byte[] bytes = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream =  new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(message);
            bytes = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    @Nullable
    public static CipherText deserialize(byte[] bytes) {
        CipherText message = null;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            message = (CipherText) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return message;
    }
}