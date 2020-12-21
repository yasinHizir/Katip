package com.crypto.katip.cryptography;

import com.crypto.katip.communication.Envelope;
import com.crypto.katip.communication.KeyServer;
import com.crypto.katip.database.models.User;

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

public class SignalCipher {
    private final SignalStore store;

    public SignalCipher(User user) {
        this.store = user.getStore();
    }

    public void encrypt(SignalProtocolAddress remoteAddress, String text, EncrypctionCallBack callBack) {
        SessionCipher cipher = new SessionCipher(store, remoteAddress);

        if (!store.containsSession(remoteAddress)) {
            KeyServer.receive(remoteAddress, keyBundle -> {
                SessionBuilder builder = new SessionBuilder(store, remoteAddress);
                try {
                    builder.process(keyBundle.toPreKeyBundle());
                } catch (InvalidKeyException | UntrustedIdentityException e) {
                    e.printStackTrace();
                }
                try {
                    CiphertextMessage ciphertextMessage = cipher.encrypt(text.getBytes());
                    callBack.handleCipherText(ciphertextMessage);
                } catch (UntrustedIdentityException e) {
                    e.printStackTrace();
                }
            });
        } else {
            try {
                CiphertextMessage ciphertextMessage = cipher.encrypt(text.getBytes());
                callBack.handleCipherText(ciphertextMessage);
            } catch (UntrustedIdentityException e) {
                e.printStackTrace();
            }
        }
    }

    public String decrypt(Envelope envelope) throws LegacyMessageException, InvalidMessageException, InvalidVersionException, DuplicateMessageException, InvalidKeyIdException, UntrustedIdentityException, InvalidKeyException, NoSessionException {
        SessionCipher cipher = new SessionCipher(store, new SignalProtocolAddress(envelope.getUsername(), envelope.getDeviceId()));
        byte[] text;

        if (envelope.getType() == CiphertextMessage.WHISPER_TYPE) {
            text = cipher.decrypt(new SignalMessage(envelope.getBody()));
        } else if (envelope.getType() == CiphertextMessage.PREKEY_TYPE){
            text = cipher.decrypt(new PreKeySignalMessage(envelope.getBody()));
        } else {
            //TODO:Tanınlanamayan mesaj için istisna oluşturabilirsin.
            text = null;
        }

        return new String(text);
    }

    public interface EncrypctionCallBack {
        void handleCipherText(CiphertextMessage cipherTextMessage);
    }
}
