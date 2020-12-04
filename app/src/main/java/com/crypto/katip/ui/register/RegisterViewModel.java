package com.crypto.katip.ui.register;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> result = new MutableLiveData<>();

    public void register(String username, String password, Context context) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper(context));
        userDatabase.save(username, password);

        if (userDatabase.isRegistered(username, password)) {
            result.setValue(new RegisterResult(username, password));
        } else {
            result.setValue(new RegisterResult("Kullanıcı sisteme kayıtlanamadı."));
        }
    }

    public void dataChanged(String username, String password, String passwordVerify) {
        if (username.equals("")) {
            formState.setValue(new RegisterFormState("Kullanıcı adı boş bırakılamaz", null, null));
        } else if (password.equals("") || password.trim().length() < 5) {
            formState.setValue(new RegisterFormState(null, "Şifre 5 harften büyük olmalı", null));
        } else if (!password.equals(passwordVerify)) {
            formState.setValue(new RegisterFormState( null, null, "Lütfen şifreyi doğrulayın."));
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
