package com.crypto.katip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.crypto.katip.ui.login.LoginResult;
import com.crypto.katip.ui.login.LoginViewModel;
import com.crypto.katip.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {
    public LoginViewModel viewModel;

    EditText usernameTextEdit;
    EditText passwordTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        usernameTextEdit = findViewById(R.id.username);
        passwordTextEdit = findViewById(R.id.password);

        viewModel.getResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult.getError() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getError(), Toast.LENGTH_LONG).show();
                } else if (loginResult.getSuccess() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getSuccess().getUsername(), Toast.LENGTH_LONG).show();
                //    startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                    finish();
                }
            }
        });
    }


    public void login(View view) {
        viewModel.login(usernameTextEdit.getText().toString(), passwordTextEdit.getText().toString(), getApplicationContext());
    }

    public void registerPage(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}