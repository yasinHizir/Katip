package com.crypto.katip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.crypto.katip.controllers.SessionsController;
import com.crypto.katip.models.User;

public class LoginActivity extends AppCompatActivity {
    SessionsController session;

    EditText usernameTextEdit;
    EditText passwordTextEdit;
    TextView notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionsController(getApplicationContext());

        if (session.isLoggedIn() == true) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }

        usernameTextEdit = findViewById(R.id.username);
        passwordTextEdit = findViewById(R.id.password);
        notice = findViewById(R.id.notice);
    }


    @SuppressLint("SetTextI18n")
    public void login(View view) {
        String username = usernameTextEdit.getText().toString();
        String password = passwordTextEdit.getText().toString();

        User user = new User(username, password);

        if (username.equals("") || password.equals("")) {
            notice.setText("Kullanıcı adı veya şifre boş bırakılamaz!");
        } else if (!user.isRegistered(getApplicationContext())) {
            notice.setText("Kullanıcı adı veya şifre hatalı!");
        } else {
            if (user.isRegistered(getApplicationContext())){
                session.createLoginSession(user);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                this.finish();
            }
        }
    }

    public void registerPage(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        this.finish();
    }
}