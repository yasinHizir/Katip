package com.crypto.katip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crypto.katip.models.User;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) throws InterruptedException {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        TextView notice = findViewById(R.id.notice);

        User user = new User();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        if ( user.getUsername().equals("") || user.getPassword().equals("") ) {
            notice.setText("Kullanıcı adı veya şifre boş bırakılamaz!");

        }else if (!user.isRegistered(getApplicationContext())) {
            notice.setText("Kullanıcı adı veya şifre hatalı!");
        }
    }

    public void registerPage(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}