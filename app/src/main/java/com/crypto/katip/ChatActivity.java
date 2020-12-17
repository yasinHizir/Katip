package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.crypto.katip.communication.MessageReceiverService;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.database.models.LoggedInUser;
import com.crypto.katip.ui.chat.ChatViewModel;
import com.crypto.katip.ui.chat.ChatViewModelFactory;

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
        setToolbar(interlocutor);
        chat = new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).getChat(interlocutor);

        viewModel.getLiveData().observe(this, messages -> viewModel.refreshRecycleView(recyclerView, new LinearLayoutManager(getApplicationContext())));
        viewModel.getLiveData().setValue(new MessageDatabase(new DbHelper(getApplicationContext()), chat.getId()).getMessages());
        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_chat_menu, menu);
        return true;
    }

    public void send(View view) {
        String text = messageEditText.getText().toString();
        viewModel.send(text, chat.getInterlocutor(), chat.getId(), getApplicationContext());
        messageEditText.setText("");
    }

    public void remove(MenuItem item) {
        new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).remove(chat.getId());
        startActivity(new Intent(ChatActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private void setToolbar(String title) {
        setSupportActionBar(findViewById(R.id.chat_bar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), MessageReceiverService.class);
        intent.putExtra(MessageReceiverService.USERNAME, user.getUsername());
        intent.putExtra(MessageReceiverService.CHAT_ID, chat.getId());
        startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(getApplicationContext(), MessageReceiverService.class);
        stopService(intent);
    }
}