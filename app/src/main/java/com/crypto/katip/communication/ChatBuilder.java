package com.crypto.katip.communication;

import android.content.Context;

import com.crypto.katip.cryptography.PublicKeyBundle;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SessionBuilder;
import org.whispersystems.libsignal.SessionCipher;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.UntrustedIdentityException;

import java.util.UUID;

public class ChatBuilder {
    private final SignalProtocolAddress remoteAddress;
    private final int userID;
    private final UUID remoteUUID;
    private String interlocutor;

    public ChatBuilder(int userID, UUID remoteUUID) {
        this.remoteAddress = new SignalProtocolAddress(remoteUUID.toString(), 0);
        this.userID = userID;
        this.remoteUUID = remoteUUID;
    }

    public boolean build(Context context) {
        User user = new UserDatabase(new DbHelper(context)).getUser(userID, context);

        if (user == null) {
            return false;
        } else if (!buildSession(user)) {
            return false;
        } else if (!sendStartChatMessage(user)) {
            return false;
        }
        new ChatDatabase(new DbHelper(context), userID).save(remoteUUID, interlocutor);

        return true;
    }

    private boolean buildSession(User user) {
        SessionBuilder builder = new SessionBuilder(user.getStore(), remoteAddress);

        try {
            PublicKeyBundle keyBundle = KeyServer.receive(remoteUUID);
            if (keyBundle == null) {
                return false;
            }
            builder.process(keyBundle.getPreKeyBundle());
            interlocutor = keyBundle.getUsername();
        } catch (InvalidKeyException | UntrustedIdentityException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean sendStartChatMessage(User user) {
        SessionCipher cipher = new SessionCipher(user.getStore(), remoteAddress);

        try {
            new MessageSender().send(remoteUUID, new Envelope(Envelope.START_CHAT_TYPE, user.getUuid(), user.getUsername(), cipher.encrypt("".getBytes()).serialize()));
        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}