package com.crypto.katip.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.database.models.Chat;

import java.util.List;

/**
 * The HomeViewModel preparing and managing the data for {@link com.crypto.katip.HomeActivity}
 */
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Chat>> liveData = new MutableLiveData<>();

    public void refreshRecycleView(RecyclerView recyclerView, LinearLayoutManager layout) {
        ChatsViewAdapter adapter = new ChatsViewAdapter(liveData.getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
    }

    public MutableLiveData<List<Chat>> getLiveData() {
        return this.liveData;
    }
}