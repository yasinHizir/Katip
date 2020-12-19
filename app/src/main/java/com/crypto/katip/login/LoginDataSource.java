package com.crypto.katip.login;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.crypto.katip.database.models.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class LoginDataSource {

    @SuppressLint("SdCardPath")
    private final File file = new File("/data/data/com.crypto.katip/cache", "User.dat");

    public boolean login(User user, Context context) {
        boolean result = false;

        try {
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    context,
                    this.file,
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            OutputStream stream = encryptedFile.openFileOutput();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(user);
            objectStream.flush();
            objectStream.close();
            result = true;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Nullable
    public User getLoggedInUser(Context context) {
        User user = null;

        try {
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            if (this.file.exists()) {
                EncryptedFile encryptedFile = new EncryptedFile.Builder(
                        context,
                        this.file,
                        masterKey,
                        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build();

                InputStream stream = encryptedFile.openFileInput();
                ObjectInputStream objectStream = new ObjectInputStream(stream);
                user = (User) objectStream.readObject();
            }
        } catch (GeneralSecurityException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean logout() {
        return this.file.delete();
    }
}
