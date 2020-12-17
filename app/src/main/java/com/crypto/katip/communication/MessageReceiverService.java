package com.crypto.katip.communication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.whispersystems.libsignal.SignalProtocolAddress;

public class MessageReceiverService extends Service {
    public static final String USERNAME = "username";
    public static final String RECEIVED_MESSAGE = "receivedMessage";

    private MessageReceiveTask task;
    private final Intent intent = new Intent();
    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String localAddress = intent.getStringExtra(USERNAME);
        task = new MessageReceiveTask(localAddress);
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

        public MessageReceiveTask(String localAddress) {
            this.localAddress = localAddress;
        }

        @Override
        public void run() {
            new MessageReceiver().receive(new SignalProtocolAddress(localAddress, 0), message -> {
                Log.v("Durum", "Thread içerisinde çalıştı");
                intent.putExtra(RECEIVED_MESSAGE, message);
                broadcastManager.sendBroadcast(intent);
            });
        }
    }
}
