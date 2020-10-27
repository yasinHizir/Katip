package com.crypto.katip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

    @SuppressLint("SetTextI18n")
    public void login(View view) {
        EditText usernameTextEdit = findViewById(R.id.username);
        EditText passwordTextEdit = findViewById(R.id.password);
        TextView notice = findViewById(R.id.notice);

        String username = usernameTextEdit.getText().toString();
        String password = passwordTextEdit.getText().toString();

        if ( username.equals("") || password.equals("") ) {
            notice.setText("Kullanıcı adı veya şifre boş bırakılamaz!");
        }else if (!User.isRegistered(username, password, getApplicationContext())) {
            notice.setText("Kullanıcı adı veya şifre hatalı!");
        }else {
            User user = User.getUser(username, getApplicationContext());
            notice.setText(user.getUsername());
        }
    }

    public void registerPage(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}