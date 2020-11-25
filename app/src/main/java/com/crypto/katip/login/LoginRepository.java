package com.crypto.katip.login;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.LoggedInUser;
import com.crypto.katip.models.User;
import com.crypto.katip.viewmodels.login.LoginResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class LoginRepository {
    private static volatile LoginRepository instance;
    private final LoginDataSource dataSource;
    private LoggedInUser user;

    public LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(Context context){
        if (instance == null){
            instance = new LoginRepository(new LoginDataSource(context));
        }
        return instance;
    }

    public LoginResult login(User user, Context context) {
        user = user.isRegistered();
        if (user != null && dataSource.login(new LoggedInUser(user.getId(), user.getUsername()))) {
            return new LoginResult(new LoggedInUser(user.getId(), user.getUsername()));
        }
        return new LoginResult("Kullanıcı sistemde kayıtlı değil.");
    }

    public boolean isLoggedIn(Context context) {
        if (this.user == null) {
            this.user = dataSource.getLoggedInUser();
        }

        return this.user != null;
    }

    public void logout() {
        if (dataSource.logout()) {
            this.user = null;
        }
    }

    public LoggedInUser getUser(){
        return this.user;
    }
}
