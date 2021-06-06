package com.crypto.katip.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.database.models.Chat;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Chat>> liveData = new MutableLiveData<>();

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager layout) {
        ChatsViewAdapter adapter = new ChatsViewAdapter(liveData.getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
    }

    public MutableLiveData<ArrayList<Chat>> getLiveData() {
        return this.liveData;
    }
}