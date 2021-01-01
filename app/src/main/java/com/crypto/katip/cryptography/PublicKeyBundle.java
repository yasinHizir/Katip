package com.crypto.katip.cryptography;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.state.PreKeyBundle;

import java.io.Serializable;

public class PublicKeyBundle implements Serializable {
    private final int registrationId;
    private final int deviceId;
    private final int preKeyId;
    private final byte[] preKeyPublic;
    private final int signedPreKeyId;
    private final byte[] signedPreKeyPublic;
    private final byte[] signedPreKeySignature;
    private final byte[] identityKey;

    public PublicKeyBundle(int registrationId, int deviceId, int preKeyId, ECPublicKey preKeyPublic, int signedPreKeyId, ECPublicKey signedPreKeyPublic, byte[] signedPreKeySignature, IdentityKey identityKey) {
        this.registrationId        = registrationId;
        this.deviceId              = deviceId;
        this.preKeyId              = preKeyId;
        this.preKeyPublic          = preKeyPublic.serialize();
        this.signedPreKeyId        = signedPreKeyId;
        this.signedPreKeyPublic    = signedPreKeyPublic.serialize();
        this.signedPreKeySignature = signedPreKeySignature;
        this.identityKey           = identityKey.serialize();
    }

    public PreKeyBundle toPreKeyBundle() {
        return new PreKeyBundle(this.getRegistrationId(), this.getDeviceId(), this.getPreKeyId(), this.getPreKey(), this.getSignedPreKeyId(), this.getSignedPreKey(), this.getSignedPreKeySignature(), this.getIdentityKey());
    }

    private int getDeviceId() {
        return deviceId;
    }

    private int getPreKeyId() {
        return preKeyId;
    }

    private ECPublicKey getPreKey() {
        try {
            return Curve.decodePoint(preKeyPublic, 0);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getSignedPreKeyId() {
        return signedPreKeyId;
    }

    private ECPublicKey getSignedPreKey() {
        try {
            return Curve.decodePoint(signedPreKeyPublic, 0);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getSignedPreKeySignature() {
        return signedPreKeySignature;
    }

    private IdentityKey getIdentityKey() {
        try {
            return new IdentityKey(identityKey, 0);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getRegistrationId() {
        return registrationId;
    }
}
