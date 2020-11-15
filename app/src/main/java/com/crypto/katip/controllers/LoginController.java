package com.crypto.katip.controllers;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.crypto.katip.database.DbHelper;
import com.crypto.katip.models.LoggedInUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class LoginController {
    private static volatile LoginController instance;
    private LoggedInUser user;

    public static LoginController getInstance(){
        if (instance == null){
            instance = new LoginController();
        }
        return instance;
    }

    public void login(String username, String password, Context context) {
        UserController userController = new UserController(new DbHelper(context));

        if (userController.isRegistered(username, password)) {
            if (setLoggedInUser(new LoggedInUser(Objects.requireNonNull(userController.getUser(username)).getId(), username), context)) {
                getLoggedInUser(context);
            }
        }
    }

    public boolean isLoggedIn(Context context) {
        if (this.user == null) {
            getLoggedInUser(context);
        }

        return this.user != null;
    }

    public void logout() {
        removeLoggedInUser();
    }

    public LoggedInUser getUser(){
        return user;
    }

    private boolean setLoggedInUser(LoggedInUser user, Context context) {
        boolean result = false;
        try {
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            @SuppressLint("SdCardPath")
            EncryptedFile file = new EncryptedFile.Builder(
                    context,
                    new File("/data/data/com.crypto.katip/cache", "LoggedInUser.txt"),
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            OutputStream stream = file.openFileOutput();
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

    private void getLoggedInUser(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            @SuppressLint("SdCardPath")
            EncryptedFile file = new EncryptedFile.Builder(
                    context,
                    new File("/data/data/com.crypto.katip/cache", "LoggedInUser.txt"),
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            InputStream stream = file.openFileInput();
            ObjectInputStream objectStream = new ObjectInputStream(stream);

            this.user = (LoggedInUser) objectStream.readObject();
        } catch (GeneralSecurityException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void removeLoggedInUser() {
        @SuppressLint("SdCardPath")
        File file = new File("/data/data/com.crypto.katip/cache", "LoggedInUser.txt");

        if (file.delete()) {
            this.user = null;
        }
    }
}
