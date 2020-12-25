package com.crypto.katip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;

public class SettingActivity extends AppCompatActivity {
    private User user;

    private EditText usernameEditText;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setToolbar();

        user = LoginRepository.getInstance().getUser();

        TextView idTextView = findViewById(R.id.text_uuid);
        idTextView.setText(user.getUuid().toString());

        TextView usernameTextView = findViewById(R.id.text_view_username);
        usernameTextView.setText(user.getUsername());

        usernameEditText = findViewById(R.id.text_edit_new_username);
        oldPasswordEditText = findViewById(R.id.text_edit_old_password);
        newPasswordEditText = findViewById(R.id.text_edit_new_password);
    }

    public void update(View view) {
        UserDatabase database = new UserDatabase(new DbHelper(getApplicationContext()));

        String newUsername = usernameEditText.getText().toString();
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();

        if (newUsername.trim().equals("")) {
            usernameEditText.setError(getString(R.string.error_username));
        } else if (newPassword.trim().equals("") || newPassword.trim().length() < 5) {
            newPasswordEditText.setError(getString(R.string.error_password));
        } else if (database.isRegistered(user.getUsername(), oldPassword)) {
            database.update(user.getId(), newUsername, newPassword);

            LoginRepository.getInstance().logout();
            LoginRepository.getInstance().login(newUsername, newPassword, getApplicationContext());

            startActivity(new Intent(SettingActivity.this, HomeActivity.class));
            finish();
        } else {
            oldPasswordEditText.setError(getString(R.string.error_old_password_verification));
        }
    }

    private void setToolbar() {
        setSupportActionBar(findViewById(R.id.setting_bar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_profile);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void delete(View view) {
        UserDatabase database = new UserDatabase(new DbHelper(getApplicationContext()));

        database.remove(user.getId());

        LoginRepository.getInstance().logout();
        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
        finish();
    }
}