package com.crypto.katip.cryptography;

import androidx.annotation.Nullable;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.state.PreKeyBundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The PublicKeyBundle class represents pre-key bundles
 * to send to the server.
 *
 * @author  Yasin HIZIR
 * @version Beta
 * @since   2021-06-17
 */
public class PublicKeyBundle implements Serializable {
    private final String    username;
    private final int       registrationId;
    private final int       deviceId;
    private final int       preKeyId;
    private final byte[]    preKeyPublic;
    private final int       signedPreKeyId;
    private final byte[]    signedPreKeyPublic;
    private final byte[]    signedPreKeySignature;
    private final byte[]    identityKey;

    public PublicKeyBundle(String username, PreKeyBundle preKeyBundle) {
        this.username              = username;
        this.registrationId        = preKeyBundle.getRegistrationId();
        this.deviceId              = preKeyBundle.getDeviceId();
        this.preKeyId              = preKeyBundle.getPreKeyId();
        this.preKeyPublic          = preKeyBundle.getPreKey().serialize();
        this.signedPreKeyId        = preKeyBundle.getSignedPreKeyId();
        this.signedPreKeyPublic    = preKeyBundle.getSignedPreKey().serialize();
        this.signedPreKeySignature = preKeyBundle.getSignedPreKeySignature();
        this.identityKey           = preKeyBundle.getIdentityKey().serialize();
    }

    /**
     *  This method receives pre-key bundle to represent
     *  this object
     *
     * @return returns pre-key bundle
     */
    @Nullable
    public PreKeyBundle getPreKeyBundle() {
        try {
            return new PreKeyBundle(
                    registrationId,
                    deviceId,
                    preKeyId,
                    Curve.decodePoint(preKeyPublic, 0),
                    signedPreKeyId,
                    Curve.decodePoint(signedPreKeyPublic, 0),
                    signedPreKeySignature,
                    new IdentityKey(identityKey, 0)
            );
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    @Nullable
    public static byte[] serialize(PublicKeyBundle keyBundle) {
        byte[] bytes = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream =  new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(keyBundle);
            bytes = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    @Nullable
    public static PublicKeyBundle deserialize(byte[] bytes) {
        PublicKeyBundle bundle = null;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {

            bundle = (PublicKeyBundle) inputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return bundle;
    }
}
