package com.crypto.katip.viewmodels.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.User;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginResult> result = new MutableLiveData<>();

    public MutableLiveData<LoginResult> getResult() {
        return result;
    }

    public void login(String username, String password, Context context) {
        LoginRepository loginRepository = LoginRepository.getInstance();
        User user = new User(username, password, new DbHelper(context));

        loginRepository.login(user, context);
        if (loginRepository.getUser() != null) {
            result.setValue(new LoginResult(loginRepository.getUser()));
        } else {
            result.setValue(new LoginResult("Kullanıcı sistemde kayıtlı değil."));
        }
    }
}
