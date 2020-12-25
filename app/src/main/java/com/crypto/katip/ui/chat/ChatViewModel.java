package com.crypto.katip.ui.chat;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.communication.MessageSender;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.database.models.TextMessage;
import com.crypto.katip.database.models.User;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<TextMessage>> liveData = new MutableLiveData<>();

    public void send(String text, User user, Chat chat, Context context) {
        MessageDatabase messageDatabase = new MessageDatabase(new DbHelper(context), chat.getId());

        new MessageSender().send(user, chat, text, message -> {
            messageDatabase.save(text, true);
            liveData.postValue(messageDatabase.getMessages());
        });
    }

    public MutableLiveData<ArrayList<TextMessage>> getLiveData() {
        return this.liveData;
    }
}