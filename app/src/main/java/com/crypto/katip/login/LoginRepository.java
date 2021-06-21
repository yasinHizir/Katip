package com.crypto.katip.login;

import android.content.Context;

import com.crypto.katip.R;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.ui.login.LoginResult;

/**
 * This class manages login and logout transactions.
 */
public class LoginRepository {
    private static volatile LoginRepository instance;
    private final LoginDataSource dataSource;
    private User user;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method receives login repository object
     *
     * @return login repository
     */
    public static LoginRepository getInstance(){
        if (instance == null){
            instance = new LoginRepository(new LoginDataSource());
        }
        return instance;
    }

    /**
     * This method logs in the application with username and password.
     *
     * @param username  Username of the user to login
     * @param password  Password of the user to login
     * @param context   Application context
     * @return          Login success or error
     */
    public LoginResult login(String username, String password, Context context) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper(context));
        User user = userDatabase.get(username, context);

        if (userDatabase.isRegistered(username, password) && user != null) {

            if (dataSource.login(user, context)) {
                this.user = user;
                return new LoginResult(true);
            }
        }

        return new LoginResult(R.string.error_user_not_registered);
    }

    /**
     * Is any user logged?
     *
     * @param context   Application context
     * @return          logged or not logged
     */
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
