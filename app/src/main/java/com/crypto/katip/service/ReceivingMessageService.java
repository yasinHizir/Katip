package com.crypto.katip.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crypto.katip.communication.Envelope;
import com.crypto.katip.communication.MessageReceiver;
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

public class ReceivingMessageService extends Service {
    public static final String RECEIVE_MESSAGE = "receive_message";
    public static final String USERID = "userID";
    public static final String CHAT_ID = "chat_id";

    private final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
    private final Intent intent = new Intent(RECEIVE_MESSAGE);
    private MessageReceiveTask task;
    private User user;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int userId = intent.getIntExtra(USERID, -1);
        this.user = new UserDatabase(new DbHelper(getApplicationContext())).getUser(userId, getApplicationContext());
        task = new MessageReceiveTask();
        if (user == null) {
            onDestroy();
        }
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
            new MessageReceiver().receive(user.getUuid(), envelope -> {
                ChatDatabase chatDatabase = new ChatDatabase(new DbHelper(getApplicationContext()), user.getId());

                if (!chatDatabase.isRegistered(envelope.getUuid())) {
                    chatDatabase.save(envelope.getUuid(), envelope.getUsername());
                }

                Chat chat = chatDatabase.getChat(envelope.getUuid());
                if (chat == null) {
                    return;
                } else if (!chat.getInterlocutor().equals(envelope.getUsername())) {
                    chatDatabase.update(chat.getId(), chat.getRemoteUUID(), envelope.getUsername());
                }

                if (envelope.getType() == Envelope.START_CHAT_TYPE) {
                    buildChat(envelope.getUuid(), envelope.getMessage());
                } else if (envelope.getType() == Envelope.CIPHERTEXT_TYPE) {
                    CipherText cipherTextMessage = CipherText.deserialize(envelope.getMessage());
                    if (cipherTextMessage == null) {
                        return;
                    }
                    decryptTextMessage(chat.getId(), envelope.getUuid(), cipherTextMessage.getCiphertextMessageType(), cipherTextMessage.getCiphertext());
                }
            });
        }

        private void buildChat(UUID remoteUUID, byte[] ciphertext) {
            try {
                new SignalCipher(user.getStore()).decrypt(remoteUUID, CiphertextMessage.PREKEY_TYPE, ciphertext);
            } catch (LegacyMessageException | InvalidMessageException | InvalidVersionException | DuplicateMessageException | InvalidKeyIdException | UntrustedIdentityException | InvalidKeyException | NoSessionException e) {
                e.printStackTrace();
            }
        }

        private void decryptTextMessage(int chatID, UUID remoteUUID, int encryption_type, byte[] message) {
            try {
                String text = new SignalCipher(user.getStore()).decrypt(remoteUUID, encryption_type, message);
                new MessageDatabase(new DbHelper(getApplicationContext()), chatID).save(text, false);
            } catch (LegacyMessageException | InvalidMessageException | InvalidVersionException | DuplicateMessageException | InvalidKeyIdException | UntrustedIdentityException | InvalidKeyException | NoSessionException e) {
                e.printStackTrace();
                return;
            }

            intent.putExtra(CHAT_ID, chatID);
            localBroadcastManager.sendBroadcast(intent);
        }
    }
}