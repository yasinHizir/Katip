package com.crypto.katip.cryptography;

import android.content.Context;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.IdentityKeyDatabase;
import com.crypto.katip.database.PreKeyDatabase;
import com.crypto.katip.database.SessionDatabase;
import com.crypto.katip.database.SignedPreKeyDatabase;
import com.crypto.katip.database.UserDatabase;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.util.List;

public class SignalStore implements SignalProtocolStore {
    private final int userId;
    private final Context context;

    public SignalStore(int userId, Context context){
        this.userId = userId;
        this.context = context;
    }

    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        return new UserDatabase(new DbHelper(context)).getIdentityKeyPair(userId);
    }

    @Override
    public int getLocalRegistrationId() {
        return new UserDatabase(new DbHelper(context)).getRegistrationID(userId);
    }

    @Override
    public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        IdentityKeyDatabase database = new IdentityKeyDatabase(new DbHelper(context),userId);
        IdentityKey verifyIdentityKey = database.get(address.toString());
        if (identityKey.equals(verifyIdentityKey)) {
            return true;
        }
        return database.save(address.toString(), identityKey);
    }

    @Override
    public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey, Direction direction) {
        IdentityKey ourRegisteredKey = new IdentityKeyDatabase(new DbHelper(context), userId).get(address.toString());
        if (ourRegisteredKey != null) {
            return ourRegisteredKey.equals(identityKey);
        }

        return true;
    }

    @Override
    public IdentityKey getIdentity(SignalProtocolAddress address) {
        return new IdentityKeyDatabase(new DbHelper(context), userId).get(address.toString());
    }

    @Override
    public PreKeyRecord loadPreKey(int preKeyId) {
        return new PreKeyDatabase(new DbHelper(context), userId).load(preKeyId);
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        new PreKeyDatabase(new DbHelper(context), userId).store(preKeyId, record);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        return new PreKeyDatabase(new DbHelper(context), userId).contain(preKeyId);
    }

    @Override
    public void removePreKey(int preKeyId) {
        new PreKeyDatabase(new DbHelper(context), userId).remove(preKeyId);
    }

    @Override
    public SessionRecord loadSession(SignalProtocolAddress address) {
        SessionRecord record = new SessionDatabase(new DbHelper(context), userId).load(address);
        if (record == null) {
            return new SessionRecord();
        }
        return record;
    }

    @Override
    public List<Integer> getSubDeviceSessions(String name) {
        return new SessionDatabase(new DbHelper(context), userId).getSubDevices(name);
    }

    @Override
    public void storeSession(SignalProtocolAddress address, SessionRecord record) {
        new SessionDatabase(new DbHelper(context), userId).store(address, record);
    }

    @Override
    public boolean containsSession(SignalProtocolAddress address) {
        return new SessionDatabase(new DbHelper(context), userId).contain(address);
    }

    @Override
    public void deleteSession(SignalProtocolAddress address) {
        new SessionDatabase(new DbHelper(context), userId).delete(address);
    }

    @Override
    public void deleteAllSessions(String name) {
        new SessionDatabase(new DbHelper(context), userId).deleteAll(name);
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) {
        return new SignedPreKeyDatabase(new DbHelper(context), userId).load(signedPreKeyId);
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return new SignedPreKeyDatabase(new DbHelper(context), userId).loadAll();
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        new SignedPreKeyDatabase(new DbHelper(context), userId).store(signedPreKeyId, record);
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return new SignedPreKeyDatabase(new DbHelper(context), userId).contain(signedPreKeyId);
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        new SignedPreKeyDatabase(new DbHelper(context), userId).remove(signedPreKeyId);
    }
}
