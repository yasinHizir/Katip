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

import com.crypto.katip.communication.MessageReceiverService;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.MessageDatabase;
import com.crypto.katip.database.models.TextMessage;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.models.Chat;
import com.crypto.katip.ui.chat.ChatViewModel;
import com.crypto.katip.ui.chat.ChatViewModelFactory;
import com.crypto.katip.ui.chat.MessagesViewAdapter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static final String INTERLOCUTOR = "interlocutor";

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

        Intent intent = getIntent();
        String interlocutor = intent.getStringExtra(INTERLOCUTOR);
        setToolbar(interlocutor);
        chat = new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).getChat(interlocutor);

        viewModel.getLiveData().observe(this, this::refreshRecycleView);
        viewModel.getLiveData().setValue(new MessageDatabase(new DbHelper(getApplicationContext()), chat.getId()).getMessages());

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(MessageReceiverService.RECEIVE_MESSAGE));
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
            messageEditText.setError("Lütfen mesaj yazınız");
            return;
        }
        viewModel.send(text, user, chat, getApplicationContext());
    }

    public void remove(MenuItem item) {
        new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).remove(chat.getId());
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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



    private void refreshRecycleView(ArrayList<TextMessage> messages) {
        if (messages != null) {
            MessagesViewAdapter adapter = new MessagesViewAdapter(messages);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int chatId = intent.getIntExtra(MessageReceiverService.CHAT_ID, -1);
            if (chatId == chat.getId()) {
                viewModel.getLiveData().setValue(new MessageDatabase(new DbHelper(getApplicationContext()), chatId).getMessages());
            }
        }
    };
}