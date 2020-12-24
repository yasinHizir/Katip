package com.crypto.katip.ui.register;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.cryptography.KeyManager;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> result = new MutableLiveData<>();

    public void register(String username, String password, Context context) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper(context));
        userDatabase.save(username, password);

        if (userDatabase.isRegistered(username, password)) {
            result.setValue(new RegisterResult(username, password));
            User user = userDatabase.getUser(username, context);
            if (user!= null) {
                new Thread() {
                    @Override
                    public void run() {
                        new KeyManager().createPublicKeys(user.getId(), username, context, 100);
                    }
                }.start();
            }
        } else {
            result.setValue(new RegisterResult("Kullanıcı sisteme kayıtlanamadı."));
        }
    }

    public void dataChanged(String username, String password, String passwordVerify) {
        if (username.trim().equals("")) {
            formState.setValue(new RegisterFormState(R.string.error_username, -1, -1));
        } else if (password.trim().equals("") || password.trim().length() < 5) {
            formState.setValue(new RegisterFormState(-1, R.string.error_password, -1));
        } else if (!password.equals(passwordVerify)) {
            formState.setValue(new RegisterFormState( -1, -1, R.string.error_verify_password));
        } else {
            formState.setValue(new RegisterFormState(true));
        }
    }

    public MutableLiveData<RegisterFormState> getFormState() {
        return formState;
    }

    public MutableLiveData<RegisterResult> getResult() {
        return result;
    }
}