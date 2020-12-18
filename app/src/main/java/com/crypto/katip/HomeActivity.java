package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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

import com.crypto.katip.communication.MessageReceiverService;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.models.LoggedInUser;
import com.crypto.katip.ui.fragments.ChatAdderFragment;
import com.crypto.katip.ui.home.HomeViewModel;
import com.crypto.katip.ui.home.HomeViewModelFactory;

public class HomeActivity extends AppCompatActivity {
    private HomeViewModel viewModel;
    private RecyclerView recyclerView;
    private LoggedInUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this, new HomeViewModelFactory()).get(HomeViewModel.class);
        recyclerView = findViewById(R.id.recycle_view);
        user = LoginRepository.getInstance(getApplicationContext()).getUser();
        setToolbar();

        viewModel.getLiveData().observe(this, strings -> viewModel.refreshRecycleView(recyclerView, new LinearLayoutManager(getApplicationContext())));
        viewModel.getLiveData().setValue(new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).getChatNames());
        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_menu, menu);
        return true;
    }

    public void add(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChatAdderFragment fragment = ChatAdderFragment.newInstance(viewModel, new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()));
        fragment.show(fragmentManager, "chat_adder_fragment");
    }

    public void logout(MenuItem item) {
        LoginRepository loginRepository = LoginRepository.getInstance(getApplicationContext());
        loginRepository.logout();
        stopService();
        startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
        finish();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewModel.getLiveData().setValue(new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).getChatNames());
        }
    };

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), MessageReceiverService.class);
        intent.putExtra(MessageReceiverService.USERNAME, user.getUsername());
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(MessageReceiverService.RECEIVE_MESSAGE));
        startService(intent);
    }

    private void stopService() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Intent intent = new Intent(getApplicationContext(), MessageReceiverService.class);
        stopService(intent);
    }

    private void setToolbar() {
        setSupportActionBar(findViewById(R.id.home_bar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.text_home_screen_title);
        }
    }
}