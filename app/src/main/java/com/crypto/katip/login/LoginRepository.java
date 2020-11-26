package com.crypto.katip.login;

import android.content.Context;

import com.crypto.katip.models.LoggedInUser;
import com.crypto.katip.models.User;
import com.crypto.katip.ui.login.LoginResult;

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

    public LoginResult login(User user) {
        user = user.isRegistered();
        if (user != null && dataSource.login(new LoggedInUser(user.getId(), user.getUsername()))) {
            this.user = new LoggedInUser(user.getId(), user.getUsername());
            return new LoginResult(this.user);
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
