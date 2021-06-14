package com.crypto.katip.cryptography;

import androidx.annotation.Nullable;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.util.Medium;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class KeyManager {
    private final SignalProtocolStore store;

    public KeyManager(SignalProtocolStore signalProtocolStore) {
        this.store = signalProtocolStore;
    }

    public boolean keyTimestampControl(long timeStamp) {
        boolean result = false;
        long lifeTime = TimeUnit.DAYS.toMillis(7);
        long currentTime = System.currentTimeMillis();

        if (currentTime - timeStamp <= lifeTime) {
            result = true;
        }

        return result;
    }

    public List<PreKeyBundle> generateKeyBundles(int startPreKeyId, int startSignedPreKeyId, int count) {
        List<PreKeyBundle> results = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            int preKeyId = ((startPreKeyId + i) % (Medium.MAX_VALUE-1)) + 1;
            int signedPreKeyId = ((startSignedPreKeyId + i) % (Medium.MAX_VALUE-1)) + 1;
            SignedPreKeyRecord signedPreKeyRecord = createSignedPreKey(store.getIdentityKeyPair(), signedPreKeyId);
            PreKeyBundle keyBundle = createKeyBundle(preKeyId, Objects.requireNonNull(signedPreKeyRecord));
            results.add(keyBundle);
        }

        return results;
    }

    public PreKeyBundle newKeyBundle(int preKeyId) {
        return createKeyBundle(preKeyId, Objects.requireNonNull(getSignedPreKey(store.getIdentityKeyPair())));
    }

    private PreKeyBundle createKeyBundle(int preKeyId, SignedPreKeyRecord signedPreKeyRecord) {
        IdentityKeyPair identityKeyPair = store.getIdentityKeyPair();
        int registrationId = store.getLocalRegistrationId();

        PreKeyRecord preKeyRecord = new PreKeyRecord(preKeyId, Curve.generateKeyPair());
        store.storePreKey(preKeyId, preKeyRecord);

        return new PreKeyBundle(
                registrationId, 0,
                preKeyRecord.getId(),
                preKeyRecord.getKeyPair().getPublicKey(),
                signedPreKeyRecord.getId(),
                signedPreKeyRecord.getKeyPair().getPublicKey(),
                signedPreKeyRecord.getSignature(),
                identityKeyPair.getPublicKey());
    }

    @Nullable
    private SignedPreKeyRecord getSignedPreKey(IdentityKeyPair identityKeyPair) {
        List<SignedPreKeyRecord> signedPreKeyRecords = store.loadSignedPreKeys();
        SignedPreKeyRecord signedPreKeyRecord = null;

        for (int i = 1; i <= signedPreKeyRecords.size(); i++) {
            if (keyTimestampControl(signedPreKeyRecords.get(i).getTimestamp())) {
                signedPreKeyRecord = signedPreKeyRecords.get(i);
                break;
            } else {
                store.removeSignedPreKey(signedPreKeyRecords.get(i).getId());
            }
        }

        if (signedPreKeyRecord == null) {
            signedPreKeyRecord = createSignedPreKey(identityKeyPair, 0);
        }

        return signedPreKeyRecord;
    }

    @Nullable
    private SignedPreKeyRecord createSignedPreKey(IdentityKeyPair identityKeyPair, int signedPreKeyId) {
        SignedPreKeyRecord signedPreKeyRecord;

        try {
            signedPreKeyRecord = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyId);
            store.storeSignedPreKey(signedPreKeyRecord.getId(), signedPreKeyRecord);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }

        return signedPreKeyRecord;
    }
}