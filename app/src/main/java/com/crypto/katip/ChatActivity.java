package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.models.Chat;
import com.crypto.katip.models.LoggedInUser;
import com.crypto.katip.models.TextMessage;
import com.crypto.katip.ui.chat.ChatViewModel;
import com.crypto.katip.ui.chat.ChatViewModelFactory;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private LoggedInUser user;
    private Chat chat;

    public static final String INTERLOCUTOR = "interlocutor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this, new ChatViewModelFactory()).get(ChatViewModel.class);
        recyclerView = findViewById(R.id.recycle_view_messages);
        messageEditText = findViewById(R.id.edit_text_message);
        user = LoginRepository.getInstance(getApplicationContext()).getUser();

        Intent intent = getIntent();
        String interlocutor = intent.getStringExtra(INTERLOCUTOR);
        chat = Chat.getInstance(new DbHelper(getApplicationContext()), user.getId(), interlocutor);

        viewModel.getLiveData().observe(this, new Observer<ArrayList<TextMessage>>() {
            @Override
            public void onChanged(ArrayList<TextMessage> messages) {
                viewModel.refreshRecycleView(recyclerView, new LinearLayoutManager(getApplicationContext()));
            }
        });
        viewModel.getLiveData().setValue(new MessageDatabase(new DbHelper(getApplicationContext()), chat.getId()).getMessages());
    }

    public void send(View view) {
        String text = messageEditText.getText().toString();
        MessageDatabase messageDatabase = new MessageDatabase(new DbHelper(getApplicationContext()), chat.getId());
        viewModel.addMessage(new TextMessage(chat.getId(), true, text, messageDatabase));
        messageDatabase.save(text, true);
    }
}