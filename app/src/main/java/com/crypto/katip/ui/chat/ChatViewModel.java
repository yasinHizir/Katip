package com.crypto.katip.ui.chat;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.communication.Envelope;
import com.crypto.katip.communication.MessageServer;
import com.crypto.katip.communication.messages.CipherText;
import com.crypto.katip.cryptography.SignalCipher;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.database.models.TextMessage;
import com.crypto.katip.database.models.User;

import org.whispersystems.libsignal.protocol.CiphertextMessage;

import java.util.List;

/**
 * The ChatViewModel preparing and managing the data for {@link com.crypto.katip.ChatActivity}
 */
public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<TextMessage>> liveData = new MutableLiveData<>();

    /**
     * This method sends a text message after it is encrypted.
     * If sending is successful, saves this message.
     *
     * @param userID    The id of the user who wants to send
     *                  a text message
     * @param chatID    The id of the chat that sends a text
     *                  message
     * @param text      Text to send
     * @param context   Application context
     */
    public void send(int userID, int chatID, String text, Context context) {

        new Thread() {
            @Override
            public void run() {
                DbHelper dbHelper = new DbHelper(context);
                User user = new UserDatabase(dbHelper).get(userID, context);
                if (user == null) {
                    return;
                }
                Chat chat = new ChatDatabase(dbHelper, user.getId()).get(chatID);
                if (chat == null) {
                    return;
                }
                MessageDatabase messageDatabase = new MessageDatabase(dbHelper, chat.getId());
                SignalCipher cipher = new SignalCipher(user.getStore());

                CiphertextMessage ciphertextMessage = cipher.encrypt(chat.getRemoteUUID(), text);
                if (ciphertextMessage == null){
                    return;
                }
                CipherText cipherTextMessage = new CipherText(
                        ciphertextMessage.getType(),
                        ciphertextMessage.serialize()
                );

                Envelope envelope = new Envelope(
                        Envelope.CIPHERTEXT_TYPE,
                        user.getUuid(),
                        user.getUsername(),
                        CipherText.serialize(cipherTextMessage)
                );

                if (new MessageServer().send(chat.getRemoteUUID(), envelope)) {
                    messageDatabase.save(text, true);
                    liveData.postValue(messageDatabase.get());
                }
            }
        }.start();
    }

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager layout) {
        List<TextMessage> messages = liveData.getValue();
        MessagesViewAdapter adapter = new MessagesViewAdapter(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.scrollToPosition(messages != null ? messages.size() - 1 : 0);
    }

    public MutableLiveData<List<TextMessage>> getLiveData() {
        return this.liveData;
    }
}