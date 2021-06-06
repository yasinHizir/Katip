package com.crypto.katip.ui.chat;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.communication.Envelope;
import com.crypto.katip.communication.MessageSender;
import com.crypto.katip.communication.messages.CipherText;
import com.crypto.katip.cryptography.SignalCipher;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.database.models.TextMessage;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;

import org.whispersystems.libsignal.protocol.CiphertextMessage;

import java.util.ArrayList;
import java.util.Objects;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<TextMessage>> liveData = new MutableLiveData<>();

    public void send(int userID, int chatID, String text, Context context) {

        new Thread() {
            @Override
            public void run() {
                User user = new UserDatabase(new DbHelper(context)).getUser(userID, context);
                Chat chat = new ChatDatabase(new DbHelper(context), Objects.requireNonNull(user).getId()).getChat(chatID);
                MessageDatabase messageDatabase = new MessageDatabase(new DbHelper(context), Objects.requireNonNull(chat).getId());
                SignalCipher cipher = new SignalCipher(user.getStore());

                CiphertextMessage ciphertextMessage = cipher.encrypt(chat.getRemoteUUID(), text);
                if (ciphertextMessage == null){
                    return;
                }
                CipherText cipherTextMessage = new CipherText(ciphertextMessage.getType(), ciphertextMessage.serialize());

                Envelope envelope = new Envelope(
                        Envelope.TEXT_TYPE,
                        user.getUuid(),
                        user.getUsername(),
                        CipherText.serialize(cipherTextMessage)
                );

                if (new MessageSender().send(chat.getRemoteUUID(), envelope)) {
                    messageDatabase.save(text, true);
                    liveData.postValue(messageDatabase.getMessages());
                }
            }
        }.start();
    }

    public MutableLiveData<ArrayList<TextMessage>> getLiveData() {
        return this.liveData;
    }
}