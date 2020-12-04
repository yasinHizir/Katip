package com.crypto.katip.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.database.models.TextMessage;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<TextMessage>> liveData = new MutableLiveData<>();

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager layout) {
        ArrayList<TextMessage> messages = liveData.getValue();
        if (messages != null) {
            MessagesViewAdapter adapter = new MessagesViewAdapter(messages);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    public MutableLiveData<ArrayList<TextMessage>> getLiveData() {
        return this.liveData;
    }
}