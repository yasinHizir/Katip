package com.crypto.katip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crypto.katip.controllers.SessionsController;
import com.crypto.katip.models.User;

public class RegisterActivity extends AppCompatActivity {



    TextView notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        notice = findViewById(R.id.notice);
    }

    @SuppressLint("SetTextI18n")
    public void register(View view) {
        EditText usernameTextEdit = findViewById(R.id.username);
        EditText passwordTextEdit = findViewById(R.id.password);
        EditText passwordVerify = findViewById(R.id.passwordVerify);

        String username = usernameTextEdit.getText().toString();
        String password = passwordTextEdit.getText().toString();
        String passwordVerifyStr = passwordVerify.getText().toString();

    }

    public void loginPage(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        this.finish();
    }
}