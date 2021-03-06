package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.crypto.katip.service.ReceivingMessageService;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.ui.chat.ChatViewModel;
import com.crypto.katip.ui.chat.ChatViewModelFactory;

public class ChatActivity extends AppCompatActivity {
    public static final String CHAT_ID = "chatID";

    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private User user;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this, new ChatViewModelFactory()).get(ChatViewModel.class);
        recyclerView = findViewById(R.id.recycle_view_messages);
        messageEditText = findViewById(R.id.edit_text_message);
        user = LoginRepository.getInstance().getUser();

        int chatID = getIntent().getIntExtra(CHAT_ID, -1);
        DbHelper dbHelper = new DbHelper(getApplicationContext());
        chat = new ChatDatabase(dbHelper, user.getId()).get(chatID);
        if (chat == null) {
            onDestroy();
        }
        setToolbar(chat.getInterlocutor());

        viewModel.getLiveData().observe(
                this,
                messages -> viewModel.refreshRecycleView(
                        recyclerView,
                        new LinearLayoutManager(getApplicationContext())
                )
        );
        viewModel.getLiveData().setValue(new MessageDatabase(dbHelper, chat.getId()).get());

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(ReceivingMessageService.RECEIVE_MESSAGE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_chat_menu, menu);
        return true;
    }

    public void send(View view) {
        String text = messageEditText.getText().toString();

        messageEditText.getText().clear();
        if (text.trim().equals("")) {
            messageEditText.setError(getString(R.string.error_empty_message));
            return;
        }

        viewModel.send(
                user.getId(),
                chat.getId(),
                text,
                getApplicationContext()
        );
    }

    public void remove(MenuItem item) {
        new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).remove(chat.getId());
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void setToolbar(String title) {
        setSupportActionBar(findViewById(R.id.chat_bar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int chatId = intent.getIntExtra(ReceivingMessageService.CHAT_ID, -1);
            if (chatId == chat.getId()) {
                viewModel.getLiveData().setValue(new MessageDatabase(new DbHelper(getApplicationContext()), chatId).get());
            }
        }
    };
}