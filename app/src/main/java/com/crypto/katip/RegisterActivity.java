package com.crypto.katip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.ui.register.RegisterViewModel;
import com.crypto.katip.ui.register.RegisterViewModelFactory;

public class RegisterActivity extends AppCompatActivity {
    public RegisterViewModel viewModel;
    public EditText usernameTextEdit;
    public EditText passwordTextEdit;
    public EditText passwordVerifyEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this, new RegisterViewModelFactory()).get(RegisterViewModel.class);
        usernameTextEdit = findViewById(R.id.username);
        passwordTextEdit = findViewById(R.id.password);
        passwordVerifyEdit = findViewById(R.id.passwordVerify);
        final Button button = findViewById(R.id.register);


        viewModel.getFormState().observe(this, registerFormState -> {
            if (registerFormState == null){
                return;
            }
            button.setEnabled(registerFormState.isDataValid());
            if (registerFormState.getUsernameError() != null) {
                usernameTextEdit.setError(registerFormState.getUsernameError());
            } else if (registerFormState.getPasswordError() != null) {
                passwordTextEdit.setError(registerFormState.getPasswordError());
            } else if (registerFormState.getPasswordVerifyError() != null) {
                passwordVerifyEdit.setError(registerFormState.getPasswordVerifyError());
            }
        });

        viewModel.getResult().observe(this, registerResult -> {
            if (registerResult.getError() == null) {
                LoginRepository.getInstance().login(registerResult.getUsername(), registerResult.getPassword(), getApplicationContext());
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), registerResult.getError(), Toast.LENGTH_SHORT).show();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.dataChanged(usernameTextEdit.getText().toString(), passwordTextEdit.getText().toString(), passwordVerifyEdit.getText().toString());
            }
        };
        usernameTextEdit.addTextChangedListener(textWatcher);
        passwordTextEdit.addTextChangedListener(textWatcher);
        passwordVerifyEdit.addTextChangedListener(textWatcher);
    }

    public void register(View view) {
        viewModel.register(usernameTextEdit.getText().toString(), passwordTextEdit.getText().toString(), getApplicationContext());
        usernameTextEdit.getText().clear();
        passwordTextEdit.getText().clear();
        passwordVerifyEdit.getText().clear();
    }

    public void loginPage(View view) {
        usernameTextEdit.getText().clear();
        passwordTextEdit.getText().clear();
        passwordVerifyEdit.getText().clear();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}