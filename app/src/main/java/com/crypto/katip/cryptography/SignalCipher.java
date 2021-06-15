package com.crypto.katip.cryptography;

import androidx.annotation.Nullable;

import com.crypto.katip.communication.KeyServer;

import org.whispersystems.libsignal.DuplicateMessageException;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.InvalidMessageException;
import org.whispersystems.libsignal.InvalidVersionException;
import org.whispersystems.libsignal.LegacyMessageException;
import org.whispersystems.libsignal.NoSessionException;
import org.whispersystems.libsignal.SessionBuilder;
import org.whispersystems.libsignal.SessionCipher;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.UntrustedIdentityException;
import org.whispersystems.libsignal.protocol.CiphertextMessage;
import org.whispersystems.libsignal.protocol.PreKeySignalMessage;
import org.whispersystems.libsignal.protocol.SignalMessage;

import java.util.Objects;
import java.util.UUID;

public class SignalCipher {
    private final SignalStore store;

    public SignalCipher(SignalStore userStore) {
        this.store = userStore;
    }

    @Nullable
    public CiphertextMessage encrypt(UUID remoteUUID, String text) {
        SignalProtocolAddress remoteAddress = new SignalProtocolAddress(remoteUUID.toString(), 0);
        SessionCipher cipher = new SessionCipher(store, remoteAddress);
        CiphertextMessage ciphertextMessage = null;

        if (!store.containsSession(remoteAddress)) {
            SessionBuilder builder = new SessionBuilder(store, remoteAddress);
            try {
                builder.process(Objects.requireNonNull(new KeyServer().receive(remoteUUID)).getPreKeyBundle());
                ciphertextMessage = cipher.encrypt(text.getBytes());
            } catch (InvalidKeyException | UntrustedIdentityException e) {
                e.printStackTrace();
            }
        } else {
            try {
                ciphertextMessage = cipher.encrypt(text.getBytes());
            } catch (UntrustedIdentityException e) {
                e.printStackTrace();
            }
        }
        return ciphertextMessage;
    }

    public String decrypt(UUID remoteUUID, int encryption_type, byte[] ciphertext) throws LegacyMessageException, InvalidMessageException, InvalidVersionException, DuplicateMessageException, InvalidKeyIdException, UntrustedIdentityException, InvalidKeyException, NoSessionException {
        SessionCipher cipher = new SessionCipher(store, new SignalProtocolAddress(remoteUUID.toString(), 0));
        byte[] text;

        if (encryption_type == CiphertextMessage.WHISPER_TYPE) {
            text = cipher.decrypt(new SignalMessage(ciphertext));
        } else if (encryption_type == CiphertextMessage.PREKEY_TYPE){
            text = cipher.decrypt(new PreKeySignalMessage(ciphertext));
        } else {
            text = "".getBytes();
        }

        return new String(text);
    }
}