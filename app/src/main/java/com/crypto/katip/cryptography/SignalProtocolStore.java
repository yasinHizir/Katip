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
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.util.List;

public class SignalProtocolStore implements org.whispersystems.libsignal.state.SignalProtocolStore{
    private int userId;
    private DbHelper dbHelper;

    public SignalProtocolStore(int userId, Context context){
        this.userId = userId;
        this.dbHelper = new DbHelper(context);
    }

    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        return new UserDatabase(dbHelper).getIdentityKeyPair(userId);
    }

    @Override
    public int getLocalRegistrationId() {
        return new UserDatabase(dbHelper).getRegistrationID(userId);
    }

    @Override
    public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        return new IdentityKeyDatabase(dbHelper).save(userId, address.toString(), identityKey);
    }

    @Override
    public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey, Direction direction) {
        return new IdentityKeyDatabase(dbHelper).get(userId, address.toString()) != null;
    }

    @Override
    public IdentityKey getIdentity(SignalProtocolAddress address) {
        return new IdentityKeyDatabase(dbHelper).get(userId, address.toString());
    }

    @Override
    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
        return new PreKeyDatabase(dbHelper).load(userId, preKeyId);
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        new PreKeyDatabase(dbHelper).store(userId, preKeyId, record);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        return new PreKeyDatabase(dbHelper).contain(userId, preKeyId);
    }

    @Override
    public void removePreKey(int preKeyId) {
        new PreKeyDatabase(dbHelper).remove(userId, preKeyId);
    }

    @Override
    public SessionRecord loadSession(SignalProtocolAddress address) {
        return new SessionDatabase(dbHelper).load(userId, address);
    }

    @Override
    public List<Integer> getSubDeviceSessions(String name) {
        return new SessionDatabase(dbHelper).getSubDevices(userId, name);
    }

    @Override
    public void storeSession(SignalProtocolAddress address, SessionRecord record) {
        new SessionDatabase(dbHelper).store(userId, address, record);
    }

    @Override
    public boolean containsSession(SignalProtocolAddress address) {
        return new SessionDatabase(dbHelper).contain(userId, address);
    }

    @Override
    public void deleteSession(SignalProtocolAddress address) {
        new SessionDatabase(dbHelper).delete(userId, address);
    }

    @Override
    public void deleteAllSessions(String name) {
        new SessionDatabase(dbHelper).deleteAll(userId, name);
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
        return new SignedPreKeyDatabase(dbHelper).load(userId, signedPreKeyId);
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return new SignedPreKeyDatabase(dbHelper).loadAll(userId);
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        new SignedPreKeyDatabase(dbHelper).store(userId, signedPreKeyId, record);
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return new SignedPreKeyDatabase(dbHelper).contain(userId, signedPreKeyId);
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        new SignedPreKeyDatabase(dbHelper).contain(userId, signedPreKeyId);
    }
}
