package com.crypto.katip.communication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.UntrustedIdentityException;

public class MessageReceiverService extends Service {
    public static final String USERNAME = "username";
    public static final String RECEIVE_MESSAGE = "receive_message";
    public static final String CHAT_ID = "chat_id";

    private MessageReceiveTask task;
    private ChatDatabase chatDatabase;
    private User user;
    private final Intent intent = new Intent(RECEIVE_MESSAGE);
    private final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String username = intent.getStringExtra(USERNAME);
        this.user = new UserDatabase(new DbHelper(getApplicationContext())).getUser(username, getApplicationContext());
        if (user != null) {
            chatDatabase = new ChatDatabase(new DbHelper(getApplicationContext()), user.getId());
        } else {
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
            SignalProtocolAddress localAddress = new SignalProtocolAddress(user.getUsername(), 0);
            new MessageReceiver().receive(localAddress, envelope -> {
                SignalCipher cipher = new SignalCipher(user);
                String text;
                try {
                    text = cipher.decrypt(envelope);
                } catch (LegacyMessageException | InvalidMessageException | InvalidVersionException | DuplicateMessageException | InvalidKeyIdException | UntrustedIdentityException | InvalidKeyException | NoSessionException e) {
                    e.printStackTrace();
                    return;
                }

                if (!chatDatabase.isRegistered(envelope.getUsername())) {
                    chatDatabase.save(envelope.getUsername());
                }

                Chat chat = chatDatabase.getChat(envelope.getUsername());
                if (chat != null) {
                    new MessageDatabase(new DbHelper(getApplicationContext()), chat.getId()).save(text, false);
                } else {
                    return;
                }

                intent.putExtra(CHAT_ID, chat.getId());
                localBroadcastManager.sendBroadcast(intent);
            });
        }
    }
}
