package com.crypto.katip;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.crypto.katip.login.LoginRepository;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        LoginRepository loginRepository = LoginRepository.getInstance(getApplicationContext());
        if (loginRepository.isLoggedIn(getApplicationContext())) {
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        }
    }
}