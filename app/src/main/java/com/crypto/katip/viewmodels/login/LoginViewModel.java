package com.crypto.katip.viewmodels.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.controllers.LoginController;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.User;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginResult> result = new MutableLiveData<>();

    public MutableLiveData<LoginResult> getResult() {
        return result;
    }

    public void login(String username, String password, Context context) {
        LoginController loginController = LoginController.getInstance();
        User user = new User(username, password, new DbHelper(context));

        loginController.login(user, context);
        if (loginController.getUser() != null) {
            result.setValue(new LoginResult(loginController.getUser()));
        } else {
            result.setValue(new LoginResult("Kullanıcı sistemde kayıtlı değil."));
        }
    }
}
