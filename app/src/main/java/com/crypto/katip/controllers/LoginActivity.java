package com.crypto.katip.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crypto.katip.R;
import com.crypto.katip.models.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        TextView notice = findViewById(R.id.notice);

        User user = new User();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        if ( user.getUsername().equals("") || user.getPassword().equals("") ) {
            notice.setText("Kullanıcı adı veya şifre boş bırakılamaz!");
        }else if (!UserRecords.isRegistered(getApplicationContext(), user.getUsername(), user.getPassword())) {
            notice.setText("Kullanıcı adı veya şifre hatalı!");
        }
    }
}