package com.crypto.katip.ui.chat;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.communication.Envelope;
import com.crypto.katip.communication.MessageSender;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.models.TextMessage;

import org.whispersystems.libsignal.SignalProtocolAddress;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<TextMessage>> liveData = new MutableLiveData<>();

    public void send(String text, String localUsername, String remoteUsername, int chatID, Context context) {
        MessageDatabase messageDatabase = new MessageDatabase(new DbHelper(context), chatID);
        SignalProtocolAddress remoteAddress = new SignalProtocolAddress(remoteUsername, 0);
        Envelope envelope = new Envelope(localUsername, text);

        new MessageSender().send(remoteAddress, envelope, message -> {
            messageDatabase.save(message.getBody(), true);
            liveData.setValue(messageDatabase.getMessages());
        });
    }

    public MutableLiveData<ArrayList<TextMessage>> getLiveData() {
        return this.liveData;
    }
}