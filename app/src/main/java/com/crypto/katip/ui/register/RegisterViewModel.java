package com.crypto.katip.ui.register;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.controllers.UserController;
import com.crypto.katip.models.LoggedInUser;
import com.crypto.katip.models.User;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    public void register(String username, String password) {
        UserController userController = new UserController();
        userController.save(username, password);

        if (!userController.isRegistered(username, password)) {
            registerResult.setValue(new RegisterResult("Kullanıcı sisteme kayıtlanamadı."));
        } else {
            registerResult.setValue(new RegisterResult( new LoggedInUser(userController.getUser(username).getId(), username)));
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

    public MutableLiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }
}