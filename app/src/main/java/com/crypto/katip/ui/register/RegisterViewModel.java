package com.crypto.katip.ui.register;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crypto.katip.R;
import com.crypto.katip.communication.KeyServer;
import com.crypto.katip.cryptography.KeyManager;
import com.crypto.katip.cryptography.PublicKeyBundle;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.KeyBundleDatabase;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;

import org.whispersystems.libsignal.state.PreKeyBundle;

import java.util.List;

/**
 * The RegisterViewModel preparing and managing the data for {@link com.crypto.katip.RegisterActivity}
 */
public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> result = new MutableLiveData<>();

    /**
     * This method registers the user with username and password in the
     * application. If
     *
     * @param username  The name of the user who wants to register
     * @param password  The password of the user who wants to register
     * @param context   Application context
     */
    public void register(String username, String password, Context context) {
        UserDatabase userDatabase = new UserDatabase(new DbHelper(context));
        userDatabase.save(username, password);

        if (userDatabase.isRegistered(username, password)) {
            LoginRepository loginRepository = LoginRepository.getInstance();

            loginRepository.login(username, password, context);
            result.setValue(new RegisterResult(true));

            KeyBundleSendTask task = new KeyBundleSendTask(username, context);
            task.start();
        } else {
            result.setValue(new RegisterResult(R.string.error_register));
        }
    }

    /**
     * This method checks the accuracy of the data
     *
     * @param username          The name of the user
     * @param password          The password of the user
     * @param passwordVerify    Password verification text
     */
    public void dataChanged(String username, String password, String passwordVerify) {
        if (username.trim().equals("")) {
            formState.setValue(new RegisterFormState(
                    R.string.error_username,
                    -1,
                    -1
            ));
        } else if (password.trim().equals("") || password.trim().length() < 5) {
            formState.setValue(new RegisterFormState(
                    -1,
                    R.string.error_password,
                    -1
            ));
        } else if (!password.equals(passwordVerify)) {
            formState.setValue(new RegisterFormState(
                    -1,
                    -1,
                    R.string.error_verify_password
            ));
        } else {
            formState.setValue(new RegisterFormState(true));
        }
    }

    public MutableLiveData<RegisterFormState> getFormState() {
        return formState;
    }

    public MutableLiveData<RegisterResult> getResult() {
        return result;
    }

    private static class KeyBundleSendTask extends Thread {
        private final Context context;
        private final String username;

        KeyBundleSendTask(String username, Context context) {
            this.context = context;
            this.username = username;
        }

        @Override
        public void run() {
            DbHelper dbHelper = new DbHelper(context);
            UserDatabase userDatabase = new UserDatabase(dbHelper);
            User user = userDatabase.get(username, context);

            if (user != null) {
                List<PreKeyBundle> preKeyBundles = new KeyManager(user.getStore())
                        .generateKeyBundles(0, 0, 100);

                for (PreKeyBundle preKeyBundle : preKeyBundles) {
                    if (new KeyServer().send(user.getUuid(), new PublicKeyBundle(username, preKeyBundle))) {

                        new KeyBundleDatabase(dbHelper, user.getId())
                                .save(preKeyBundle.getSignedPreKeyId(), preKeyBundle.getPreKeyId());

                    } else {
                        return;
                    }
                }
            }
        }
    }
}