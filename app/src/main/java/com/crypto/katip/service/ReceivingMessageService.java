package com.crypto.katip.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crypto.katip.communication.Envelope;
import com.crypto.katip.communication.MessageServer;
import com.crypto.katip.communication.messages.CipherText;
import com.crypto.katip.cryptography.SignalCipher;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.database.models.User;

import org.whispersystems.libsignal.DuplicateMessageException;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.InvalidMessageException;
import org.whispersystems.libsignal.InvalidVersionException;
import org.whispersystems.libsignal.LegacyMessageException;
import org.whispersystems.libsignal.NoSessionException;
import org.whispersystems.libsignal.UntrustedIdentityException;
import org.whispersystems.libsignal.protocol.CiphertextMessage;

import java.util.UUID;

/**
 * This service receives messages from the server and processes their.
 */
public class ReceivingMessageService extends Service {
    public static final String RECEIVE_MESSAGE = "receive_message";
    public static final String USERID = "userID";
    public static final String CHAT_ID = "chat_id";

    private final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
    private final Intent intent = new Intent(RECEIVE_MESSAGE);
    private User user;
    private MessageReceiveTask task;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int userId = intent.getIntExtra(USERID, -1);
        this.user = new UserDatabase(new DbHelper(getApplicationContext()))
                .get(userId, getApplicationContext());
        if (user == null) {
            onDestroy();
        }

        task = new MessageReceiveTask();
        task.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        task.interrupt();
    }

    private class MessageReceiveTask extends Thread {

        @Override
        public void run() {
            new MessageServer().receive(user.getUuid(), envelope -> {
                ChatDatabase chatDatabase = new ChatDatabase(
                        new DbHelper(getApplicationContext()),
                        user.getId()
                );

                if (!chatDatabase.isRegistered(envelope.getUuid())) {
                    chatDatabase.save(envelope.getUuid(), envelope.getUsername());
                }

                Chat chat = chatDatabase.get(envelope.getUuid());
                if (chat == null) {
                    return;
                } else if (!chat.getInterlocutor().equals(envelope.getUsername())) {
                    chatDatabase.update(chat.getId(), envelope.getUsername());
                }

                try {
                    if (envelope.getType() == Envelope.START_CHAT_TYPE) {
                        buildChat(envelope.getUuid(), envelope.getMessage());
                    } else if (envelope.getType() == Envelope.CIPHERTEXT_TYPE) {
                        CipherText cipherTextMessage = CipherText.deserialize(envelope.getMessage());
                        if (cipherTextMessage == null) {
                            return;
                        }
                        decryptTextMessage(
                                chat.getId(),
                                envelope.getUuid(),
                                cipherTextMessage.getCipherType(),
                                cipherTextMessage.getCiphertext()
                        );
                    }
                } catch (InvalidKeyIdException |
                        LegacyMessageException |
                        InvalidMessageException |
                        UntrustedIdentityException |
                        InvalidKeyException |
                        DuplicateMessageException |
                        InvalidVersionException |
                        NoSessionException e) {

                    e.printStackTrace();
                }

                intent.putExtra(CHAT_ID, chat.getId());
                localBroadcastManager.sendBroadcast(intent);
            });
        }

        private void buildChat(UUID remoteUUID, byte[] ciphertext)
                throws NoSessionException, InvalidKeyException, LegacyMessageException,
                InvalidVersionException, InvalidMessageException, DuplicateMessageException,
                InvalidKeyIdException, UntrustedIdentityException
        {
            new SignalCipher(user.getStore()).decrypt(
                    remoteUUID,
                    CiphertextMessage.PREKEY_TYPE,
                    ciphertext
            );
        }

        private void decryptTextMessage(int chatID, UUID remoteUUID, int encryption_type, byte[] message)
                throws NoSessionException, InvalidKeyException, LegacyMessageException, InvalidVersionException,
                InvalidMessageException, DuplicateMessageException, InvalidKeyIdException, UntrustedIdentityException
        {
            String text = new SignalCipher(user.getStore()).decrypt(
                    remoteUUID,
                    encryption_type,
                    message
            );
            new MessageDatabase(new DbHelper(getApplicationContext()), chatID).save(text, false);
        }
    }
}