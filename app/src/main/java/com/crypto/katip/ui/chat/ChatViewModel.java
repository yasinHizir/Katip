package com.crypto.katip.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.models.TextMessage;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<TextMessage>> liveData = new MutableLiveData<>();

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager layout) {
        MessagesViewAdapter adapter = new MessagesViewAdapter(liveData.getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
    }

    public MutableLiveData<ArrayList<TextMessage>> getLiveData() {
        return this.liveData;
    }

    public void addMessage(TextMessage message) {
        ArrayList<TextMessage> messages = getLiveData().getValue();

        if (messages != null) {
            messages.add(message);
        } else {
            ArrayList<TextMessage> newArray = new ArrayList<>();
            newArray.add(message);
            getLiveData().setValue(newArray);
        }

        getLiveData().setValue(messages);
    }
}