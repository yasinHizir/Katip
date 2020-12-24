package com.crypto.katip.ui.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.login.LoginRepository;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginResult> result = new MutableLiveData<>();

    public MutableLiveData<LoginResult> getResult() {
        return result;
    }

    public void login(String username, String password, Context context) {
        LoginRepository loginRepository = LoginRepository.getInstance();

        result.setValue(loginRepository.login(username, password, context));
    }
}
