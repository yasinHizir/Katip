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

import com.crypto.katip.service.ReceivingMessageService;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.ui.fragments.ChatAdderFragment;
import com.crypto.katip.ui.home.HomeViewModel;
import com.crypto.katip.ui.home.HomeViewModelFactory;

public class HomeActivity extends AppCompatActivity {
    private HomeViewModel viewModel;
    private RecyclerView recyclerView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this, new HomeViewModelFactory()).get(HomeViewModel.class);
        recyclerView = findViewById(R.id.recycle_view);
        user = LoginRepository.getInstance().getUser();
        setToolbar();

        viewModel.getLiveData().observe(this, chats -> viewModel.refreshRecycleView(recyclerView, new LinearLayoutManager(getApplicationContext())));
        viewModel.getLiveData().setValue(new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).getChats());
        startService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_menu, menu);
        return true;
    }

    public void add(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChatAdderFragment fragment = ChatAdderFragment.newInstance(viewModel, getApplicationContext());
        fragment.show(fragmentManager, "chat_adder_fragment");
    }

    public void settings(MenuItem item) {
        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
    }

    public void logout(MenuItem item) {
        LoginRepository loginRepository = LoginRepository.getInstance();
        loginRepository.logout();
        stopService();
        startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
        finish();
    }

    private void setToolbar() {
        setSupportActionBar(findViewById(R.id.home_bar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.text_home_screen_title);
        }
    }

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), ReceivingMessageService.class);
        intent.putExtra(ReceivingMessageService.USERID, user.getId());
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ReceivingMessageService.RECEIVE_MESSAGE));
        startService(intent);
    }

    private void stopService() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Intent intent = new Intent(getApplicationContext(), ReceivingMessageService.class);
        stopService(intent);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewModel.getLiveData().setValue(new ChatDatabase(new DbHelper(getApplicationContext()), user.getId()).getChats());
        }
    };
}