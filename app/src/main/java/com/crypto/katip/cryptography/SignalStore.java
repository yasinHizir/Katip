package com.crypto.katip.cryptography;

import android.content.Context;

import com.crypto.katip.communication.KeyServer;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.IdentityKeyDatabase;
import com.crypto.katip.database.KeyBundleDatabase;
import com.crypto.katip.database.PreKeyDatabase;
import com.crypto.katip.database.SessionDatabase;
import com.crypto.katip.database.SignedPreKeyDatabase;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.util.List;

/**
 * This class implements SignalProtocolStore interface.
 * This class manages identity keypair, identities,
 * pre-keys, signed pre-keys and sessions.
 *
 * @author  Yasin HIZIR
 * @version Beta
 * @since   2021-06-17
 */
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
        IdentityKeyDatabase identityKeyDatabase = new IdentityKeyDatabase(new DbHelper(context),userId);

        IdentityKey verifyIdentityKey = identityKeyDatabase.get(address.toString());
        if (identityKey.equals(verifyIdentityKey)) {
            return true;
        }

        return identityKeyDatabase.save(address.toString(), identityKey);
    }

    @Override
    public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey, Direction direction) {
        IdentityKeyDatabase identityKeyDatabase = new IdentityKeyDatabase(new DbHelper(context), userId);

        IdentityKey ourRegisteredKey = identityKeyDatabase.get(address.toString());
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
        return new PreKeyDatabase(new DbHelper(context), userId).get(preKeyId);
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        new PreKeyDatabase(new DbHelper(context), userId).save(preKeyId, record);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        return new PreKeyDatabase(new DbHelper(context), userId).contain(preKeyId);
    }

    @Override
    public void removePreKey(int preKeyId) {
        DbHelper dbHelper = new DbHelper(context);
        new PreKeyDatabase(dbHelper, userId).remove(preKeyId);

        new Thread() {
            @Override
            public void run() {
                User user = new UserDatabase(dbHelper).get(userId, context);
                KeyBundleDatabase keyBundleDatabase = new KeyBundleDatabase(dbHelper, userId);

                keyBundleDatabase.remove(preKeyId);
                if (user != null) {
                    PreKeyBundle preKeyBundle = new KeyManager(user.getStore()).newKeyBundle(preKeyId);
                    boolean result = new KeyServer().send(
                            user.getUuid(),
                            new PublicKeyBundle(user.getUsername(), preKeyBundle)
                    );

                    if (result) {
                        keyBundleDatabase.save(preKeyBundle.getSignedPreKeyId(), preKeyBundle.getPreKeyId());
                    }
                }
            }
        }.start();
    }

    @Override
    public SessionRecord loadSession(SignalProtocolAddress address) {
        SessionDatabase sessionDatabase = new SessionDatabase(new DbHelper(context), userId);

        SessionRecord record = sessionDatabase.get(address);
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
        SessionDatabase sessionDatabase = new SessionDatabase(new DbHelper(context), userId);

        SessionRecord oldRecord = sessionDatabase.get(address);
        if (oldRecord != null) {
            sessionDatabase.remove(address);
        }

        sessionDatabase.save(address, record);
    }

    @Override
    public boolean containsSession(SignalProtocolAddress address) {
        return new SessionDatabase(new DbHelper(context), userId).contain(address);
    }

    @Override
    public void deleteSession(SignalProtocolAddress address) {
        new SessionDatabase(new DbHelper(context), userId).remove(address);
    }

    @Override
    public void deleteAllSessions(String name) {
        new SessionDatabase(new DbHelper(context), userId).removeAll(name);
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) {
        return new SignedPreKeyDatabase(new DbHelper(context), userId).get(signedPreKeyId);
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return new SignedPreKeyDatabase(new DbHelper(context), userId).get();
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        new SignedPreKeyDatabase(new DbHelper(context), userId).save(signedPreKeyId, record);
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return new SignedPreKeyDatabase(new DbHelper(context), userId).contain(signedPreKeyId);
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        DbHelper dbHelper = new DbHelper(context);

        new SignedPreKeyDatabase(dbHelper, userId).remove(signedPreKeyId);
        List<Integer> preKeyIds = new KeyBundleDatabase(dbHelper, userId).getPreKeyIds(signedPreKeyId);
        for (Integer preKeyId : preKeyIds) {
            removePreKey(preKeyId);
        }

    }
}
