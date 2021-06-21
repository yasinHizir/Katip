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

import java.util.UUID;

/**
 * The SignalCipher class encrypt and decrypt messages.
 *
 * @author  Yasin HIZIR
 * @version Beta
 * @since   2021-06-17
 */
public class SignalCipher {
    private final SignalStore store;

    public SignalCipher(SignalStore userStore) {
        this.store = userStore;
    }

    /**
     *  This method encrypt a text.
     *
     * @param remoteUUID    Remote id of the person to send message
     * @param text          Text to encrypt
     * @return              Encrypted text
     */
    @Nullable
    public CiphertextMessage encrypt(UUID remoteUUID, String text) {
        SignalProtocolAddress remoteAddress = new SignalProtocolAddress(
                remoteUUID.toString(),
                0
        );
        SessionCipher cipher = new SessionCipher(store, remoteAddress);
        CiphertextMessage ciphertextMessage = null;

        try {
            if (!store.containsSession(remoteAddress)) {

                SessionBuilder builder = new SessionBuilder(store, remoteAddress);
                PublicKeyBundle publicKeyBundle = new KeyServer().receive(remoteUUID);

                if (publicKeyBundle != null) {
                    builder.process(publicKeyBundle.getPreKeyBundle());
                    ciphertextMessage = cipher.encrypt(text.getBytes());
                }

            } else {
                ciphertextMessage = cipher.encrypt(text.getBytes());
            }
        } catch (UntrustedIdentityException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return ciphertextMessage;
    }

    /**
     *  This method decrypt a text.
     *
     * @param remoteUUID        Remote id of the person to receive message
     * @param encryption_type   CipherType in CipherText object.
     * @return                  Decrypted text
     */
    public String decrypt(UUID remoteUUID, int encryption_type, byte[] ciphertext)
            throws LegacyMessageException, InvalidMessageException, InvalidVersionException,
            DuplicateMessageException, InvalidKeyIdException, UntrustedIdentityException,
            InvalidKeyException, NoSessionException
    {
        SessionCipher cipher = new SessionCipher(
                store,
                new SignalProtocolAddress(remoteUUID.toString(), 0)
        );
        byte[] text;

        switch (encryption_type) {

            case CiphertextMessage.WHISPER_TYPE:
                text = cipher.decrypt(new SignalMessage(ciphertext));
                break;

            case CiphertextMessage.PREKEY_TYPE:
                text = cipher.decrypt(new PreKeySignalMessage(ciphertext));
                break;

            default:
                text = "".getBytes();
        }

        return new String(text);
    }
}