package com.crypto.katip.communication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.annotation.Nullable;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;

import org.whispersystems.libsignal.SignalProtocolAddress;

public class MessageReceiverService extends Service {
    private MessageReceiveTask task;
    public static final String USERNAME = "username";
    public static final String CHAT_ID = "chatID";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String localAddress = intent.getStringExtra(USERNAME);
        int chatId = intent.getIntExtra(CHAT_ID, 0);
        task = new MessageReceiveTask(localAddress, chatId);
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

    private class MessageReceiveTask extends Thread{

        private final String localAddress;
        private final int chatId;

        public MessageReceiveTask(String localAddress, int chatId) {
            this.localAddress = localAddress;
            this.chatId = chatId;
        }

        @Override
        public void run() {
            new MessageReceiver().receive(new SignalProtocolAddress(localAddress, 0), message -> new MessageDatabase(new DbHelper(getApplicationContext()), chatId).save(message, false));
        }
    }
}
