package com.crypto.katip.viewmodels.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> liveData = new MutableLiveData<>();

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager manager) {
        ChatsViewAdapter adapter = new ChatsViewAdapter(null, liveData.getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    public MutableLiveData<ArrayList<String>> getLiveData() {
        return this.liveData;
    }

    public void addChat(String username) {
        ArrayList<String> chatNames = getLiveData().getValue();

        if (chatNames != null) {
            chatNames.add(username);
        } else {
            ArrayList<String> newArray = new ArrayList<>();
            newArray.add(username);
            getLiveData().setValue(newArray);
        }

        getLiveData().setValue(chatNames);
    }
}