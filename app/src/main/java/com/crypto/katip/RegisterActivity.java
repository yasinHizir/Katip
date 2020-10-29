package com.crypto.katip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crypto.katip.models.User;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @SuppressLint("SetTextI18n")
    public void register(View view) {
        EditText usernameTextEdit = findViewById(R.id.username);
        EditText passwordTextEdit = findViewById(R.id.password);
        EditText passwordVerify = findViewById(R.id.passwordVerify);

        TextView notice = findViewById(R.id.notice);

        String username = usernameTextEdit.getText().toString();
        String password = passwordTextEdit.getText().toString();
        String passwordVerifyStr = passwordVerify.getText().toString();


        if ( username.equals("") || password.equals("") || passwordVerifyStr.equals("")) {
            notice.setText("Lütfen istenilen alanları doğru bir şekilde doldurun");

        }else if (!password.equals(passwordVerifyStr)){
            notice.setText("Lütfen aynı şifreyi girdiğinizden emin olun");
        }else {
            if(User.save(username, password, getApplicationContext())) {
                onBackPressed();
            }else{
                notice.setText("Hata");
            }
        }
    }

    public void loginPage(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}