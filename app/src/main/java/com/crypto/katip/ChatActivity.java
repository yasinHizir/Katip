package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.models.Chat;
import com.crypto.katip.models.LoggedInUser;
import com.crypto.katip.ui.chat.ChatViewModel;
import com.crypto.katip.ui.chat.ChatViewModelFactory;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private LoggedInUser user;
    private Chat chat;

    public static final String INTERLOCUTOR = "interlocutor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this, new ChatViewModelFactory()).get(ChatViewModel.class);
        recyclerView = findViewById(R.id.recycle_view_messages);
        user = LoginRepository.getInstance(getApplicationContext()).getUser();

        Intent intent = getIntent();
        String interlocutor = intent.getStringExtra(INTERLOCUTOR);
        chat = Chat.getInstance(new DbHelper(getApplicationContext()), user.getId(), interlocutor);

        viewModel.getLiveData().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                viewModel.refreshRecycleView(recyclerView, new LinearLayoutManager(getApplicationContext()));
            }
        });
        ArrayList<String> messages = new ArrayList<>();
        messages.add("Deneme başarıyla gerçekleşmiştir.");
        messages.add("İkinci mesaj");
        messages.add("Üçüncü mesaj");
        viewModel.getLiveData().setValue(messages);
    }
}