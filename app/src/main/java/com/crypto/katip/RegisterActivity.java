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
    SessionsController session;

    EditText usernameTextEdit;
    EditText passwordTextEdit;
    EditText passwordVerify;
    TextView notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new SessionsController(getApplicationContext());

        usernameTextEdit = findViewById(R.id.username);
        passwordTextEdit = findViewById(R.id.password);
        passwordVerify = findViewById(R.id.passwordVerify);
        notice = findViewById(R.id.notice);
    }

    @SuppressLint("SetTextI18n")
    public void register(View view) {
        String username = usernameTextEdit.getText().toString();
        String password = passwordTextEdit.getText().toString();
        String passwordVerifyStr = passwordVerify.getText().toString();

        User user = new User(username, password);

        if ( username.equals("") || password.equals("") || passwordVerifyStr.equals("")) {
            notice.setText("Lütfen istenilen alanları doğru bir şekilde doldurun");
        }else if (!password.equals(passwordVerifyStr)){
            notice.setText("Lütfen aynı şifreyi girdiğinizden emin olun");
        }else {
            if(user.save(getApplicationContext())) {
                session.createLoginSession(user);
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                this.finish();
            }else{
                notice.setText("Kullanıcı adı daha önce kullanıldı!");
            }
        }
    }

    public void loginPage(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        this.finish();
    }
}