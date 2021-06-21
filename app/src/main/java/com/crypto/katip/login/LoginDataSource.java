package com.crypto.katip.login;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.crypto.katip.cryptography.SignalStore;
import com.crypto.katip.database.models.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.UUID;

/**
 * This class manages to store logged in user.
 */
class LoginDataSource {
    @SuppressLint("SdCardPath")
    private final File file = new File("/data/data/com.crypto.katip", "User.dat");
    private EncryptedFile encryptedFile;

    /**
     * This method write logged in user to the
     * encrypted file.
     *
     * @param user      Logged in user
     * @param context   Application Context
     * @return          logged or not
     */
    protected boolean login(User user, Context context) {
        boolean result = false;

        try {
            if (this.encryptedFile == null) {
                createEncryptedFile(context);
            }

            try (OutputStream stream = encryptedFile.openFileOutput();
                 ObjectOutputStream objectStream = new ObjectOutputStream(stream)) {

                LoggedInUser logged = new LoggedInUser(
                        user.getUuid().toString(),
                        user.getId(),
                        user.getUsername()
                );

                objectStream.writeObject(logged);
                objectStream.flush();
                result = true;
            }


        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This method reads logged in user from the encrypted
     * file.
     *
     * @param context   Application context
     * @return          Logged in user
     */
    @Nullable
    protected User getLoggedInUser(Context context) {
        User user = null;

        if (this.file.exists()) {
            if (this.encryptedFile == null) {
                createEncryptedFile(context);
            }

            try (InputStream stream = encryptedFile.openFileInput();
                 ObjectInputStream objectStream = new ObjectInputStream(stream)) {

                LoggedInUser logged = (LoggedInUser) objectStream.readObject();

                user = new User(
                        UUID.fromString(logged.uuid),
                        logged.getId(),
                        logged.getUsername(),
                        new SignalStore(logged.getId(), context)
                );
            } catch (GeneralSecurityException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    /**
     * This method remove logged in user from the encrypted
     * file.
     *
     * @return  removed or not removed
     */
    protected boolean logout() {
        return this.file.delete();
    }

    private void createEncryptedFile(Context context) {
        try {
            this.encryptedFile = new EncryptedFile.Builder(
                    context,
                    this.file,
                    new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

        }  catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private static class LoggedInUser implements Serializable {
        private final String uuid;
        private final int id;
        private final String username;

        private LoggedInUser(String uuid, int id, String username) {
            this.uuid = uuid;
            this.id = id;
            this.username = username;
        }

        public String getUuid() {
            return uuid;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }
    }
}
