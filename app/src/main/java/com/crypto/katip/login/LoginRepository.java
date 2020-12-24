package com.crypto.katip.login;

import android.content.Context;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.ui.login.LoginResult;

public class LoginRepository {
    private static volatile LoginRepository instance;
    private final LoginDataSource dataSource;
    private User user;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(){
        if (instance == null){
            instance = new LoginRepository(new LoginDataSource());
        }
        return instance;
    }

    public LoginResult login(String username, String password, Context context) {
        UserDatabase database = new UserDatabase(new DbHelper(context));
        User user = database.getUser(username, context);

        if (database.isRegistered(username, password) && user != null) {

            if (dataSource.login(user, context)) {
                this.user = user;
                return new LoginResult(user);
            }
        }

        return new LoginResult("Kullanıcı sistemde kayıtlı değil.");
    }

    public boolean isLoggedIn(Context context) {
        if (this.user == null) {
            this.user = dataSource.getLoggedInUser(context);
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
