package com.crypto.katip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        EditText passwordVerify = findViewById(R.id.passwordVerify);

        TextView notice = findViewById(R.id.notice);

        User user = new User();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        String passwordVerifyStr = passwordVerify.getText().toString();


        if (user.getUsername().equals("") || user.getPassword().equals("") || passwordVerifyStr.equals("")) {
            notice.setText("Lütfen istenilen alanları doğru bir şekilde doldurun");
        } else if (!user.getPassword().equals(passwordVerifyStr)) {
            notice.setText("Lütfen aynı şifreyi girdiğinizden emin olun");
        } else {
            user.save(getApplicationContext());
            onBackPressed();
        }

        User selected_user = new User();
        selected_user.find(1, getApplicationContext());
        Log.i("=======>>> User: ", selected_user.show());
    }

    public void loginPage(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}