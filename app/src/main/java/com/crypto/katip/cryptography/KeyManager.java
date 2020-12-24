package com.crypto.katip.cryptography;

import android.content.Context;

import com.crypto.katip.communication.KeyServer;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.PreKeyDatabase;
import com.crypto.katip.database.SignedPreKeyDatabase;
import com.crypto.katip.database.UserDatabase;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KeyManager {
    public boolean keyTimestampControl(long timeStamp) {
        boolean result = false;
        long lifeTime = TimeUnit.DAYS.toDays(7);
        long currentTime = System.currentTimeMillis();

        if (currentTime - timeStamp < lifeTime) {
            result = true;
        }

        return result;
    }

    public void createPublicKeys(int userId, String username, Context context, int count) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper(context));
        IdentityKeyPair identityKeyPair = userDatabase.getIdentityKeyPair(userId);
        int registrationId = userDatabase.getRegistrationID(userId);

        SignedPreKeyDatabase signedPreKeyDatabase = new SignedPreKeyDatabase(new DbHelper(context), userId);
        SignedPreKeyRecord signedPreKeyRecord;
        try {
            signedPreKeyRecord = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyDatabase.getAvailableSignedPreKeyId());
            signedPreKeyDatabase.store(signedPreKeyRecord.getId(), signedPreKeyRecord);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return;
        }

        PreKeyDatabase preKeyDatabase = new PreKeyDatabase(new DbHelper(context), userId);

        for (int i = 0; i < count; i++) {
            PreKeyRecord preKeyRecord = new PreKeyRecord(i, Curve.generateKeyPair());
            PublicKeyBundle keyBundle = new PublicKeyBundle(registrationId, 0,
                                                            preKeyRecord.getId(),
                                                            preKeyRecord.getKeyPair().getPublicKey(),
                                                            signedPreKeyRecord.getId(),
                                                            signedPreKeyRecord.getKeyPair().getPublicKey(),
                                                            signedPreKeyRecord.getSignature(),
                                                            identityKeyPair.getPublicKey());
            KeyServer.send(new SignalProtocolAddress(username, 0), keyBundle,
                    sentBundle -> preKeyDatabase.store(sentBundle.getPreKeyId(), preKeyRecord));
        }
    }

    public void newPreKey(int userId, String username, Context context, int keyId) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper(context));
        IdentityKeyPair identityKeyPair = userDatabase.getIdentityKeyPair(userId);
        int registrationId = userDatabase.getRegistrationID(userId);

        PreKeyDatabase preKeyDatabase = new PreKeyDatabase(new DbHelper(context), userId);
        PreKeyRecord preKeyRecord = new PreKeyRecord(keyId, Curve.generateKeyPair());

        SignedPreKeyDatabase  signedPreKeyDatabase = new SignedPreKeyDatabase(new DbHelper(context), userId);
        List<SignedPreKeyRecord> signedPreKeyRecords = signedPreKeyDatabase.loadAll();
        SignedPreKeyRecord signedPreKeyRecord = null;

        for (int i = 0; i < signedPreKeyRecords.size(); i++) {
            if (keyTimestampControl(signedPreKeyRecords.get(i).getTimestamp())) {
                signedPreKeyRecord = signedPreKeyRecords.get(i);
            }
        }

        if (signedPreKeyRecord == null) {
            try {
                signedPreKeyRecord = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyDatabase.getAvailableSignedPreKeyId());
                signedPreKeyDatabase.store(signedPreKeyRecord.getId(), signedPreKeyRecord);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        PublicKeyBundle keyBundle = new PublicKeyBundle(registrationId, 0,
                                                        preKeyRecord.getId(),
                                                        preKeyRecord.getKeyPair().getPublicKey(),
                                                        signedPreKeyRecord.getId(),
                                                        signedPreKeyRecord.getKeyPair().getPublicKey(),
                                                        signedPreKeyRecord.getSignature(),
                                                        identityKeyPair.getPublicKey());
        KeyServer.send(new SignalProtocolAddress(username, 0), keyBundle,
                sentBundle -> preKeyDatabase.store(sentBundle.getPreKeyId(), preKeyRecord));
    }
}