package com.crypto.katip.ui.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.login.LoginRepository;

/**
 * The LoginViewModel preparing and managing the data for {@link com.crypto.katip.LoginActivity}
 */
public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginResult> result = new MutableLiveData<>();

    /**
     * This method logins application to given username and password
     *
     * @param username  The name of the user that
     *                  wants to login application
     * @param password  The password of the user that
     *                  wants to login application
     * @param context   Application context
     */
    public void login(String username, String password, Context context) {
        LoginRepository loginRepository = LoginRepository.getInstance();

        result.setValue(loginRepository.login(username, password, context));
    }

    public MutableLiveData<LoginResult> getResult() {
        return result;
    }
}