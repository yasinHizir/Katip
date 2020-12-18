package com.crypto.katip.login;

import android.content.Context;

import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.ui.login.LoginResult;

public class LoginRepository {
    private static volatile LoginRepository instance;
    private final LoginDataSource dataSource;
    private User user;

    public LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(Context context){
        if (instance == null){
            instance = new LoginRepository(new LoginDataSource(context));
        }
        return instance;
    }

    public LoginResult login(String username, String password, UserDatabase database) {

        if (database.isRegistered(username, password)) {
            User user = database.getUser(username);
            if (dataSource.login(user)) {
                this.user = user;
                return new LoginResult(user);
            }
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

    public User getUser(){
        return this.user;
    }
}
