package com.crypto.katip.ui.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.controllers.LoginController;
import com.crypto.katip.database.DbHelper;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginResult> result = new MutableLiveData<>();

    public MutableLiveData<LoginResult> getResult() {
        return result;
    }

    public void login(String username, String password, Context context) {
        LoginController loginController = LoginController.getInstance();
        loginController.login(username, password, new DbHelper(context));
        if (loginController.getUser() != null) {
            result.setValue(new LoginResult(loginController.getUser()));
        } else {
            result.setValue(new LoginResult("Kullanıcı sistemde kayıtlı değil."));
        }
    }
}
