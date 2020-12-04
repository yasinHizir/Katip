package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.Chat;
import com.crypto.katip.models.LoggedInUser;
import com.crypto.katip.ui.fragments.ChatAdderFragment;
import com.crypto.katip.ui.home.HomeViewModel;
import com.crypto.katip.ui.home.HomeViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

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

        viewModel.getLiveData().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                viewModel.refreshRecycleView(recyclerView, new LinearLayoutManager(getApplicationContext()));
            }
        });
        viewModel.getLiveData().setValue(Chat.getChatNames(new DbHelper(getApplicationContext()), user.getId()));
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
        startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
        finish();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.home_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sohbetler");
    }
}