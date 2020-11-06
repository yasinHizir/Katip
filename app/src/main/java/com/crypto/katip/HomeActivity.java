package com.crypto.katip;

import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;

import androidx.appcompat.app.AppCompatActivity;

import com.crypto.katip.controllers.SessionsController;

public class HomeActivity extends AppCompatActivity {
    SessionsController session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        session = new SessionsController(getApplicationContext());

        if (session.isLoggedIn() == false) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
        this.finish();


    }
}