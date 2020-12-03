package com.crypto.katip.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> liveData = new MutableLiveData<>();

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager layout) {
        MessagesViewAdapter adapter = new MessagesViewAdapter(liveData.getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
    }

    public MutableLiveData<ArrayList<String>> getLiveData() {
        return this.liveData;
    }
}