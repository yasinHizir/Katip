package com.crypto.katip.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.crypto.katip.HomeActivity;
import com.crypto.katip.LoginActivity;
import com.crypto.katip.models.User;

import java.util.HashMap;

public class SessionsController {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SharedPreferencesOrnek";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public SessionsController(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(User user){
        // giriş yapıldığında login değerini true yapıyoruz.
        editor.putBoolean(IS_LOGIN, true);

        // email ve sifreyi editor ile kaydediyoruz.
        editor.putString(USERNAME, user.getUsername());
        editor.putString(PASSWORD, user.getPassword());
        editor.commit();

    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            // Kullanıcı girmemesi gereken yerde, giriş sayfasına yönlendir.
            Intent i = new Intent(_context, LoginActivity.class);
            // flagler ile her şeyi sil
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap currentUser(){
        HashMap user = new HashMap();
        user.put(USERNAME, pref.getString(USERNAME, null));
        user.put(PASSWORD, pref.getString(PASSWORD, null));
        return user;
    }

    public void logout(){

        // Shared Preferences dan tüm verileri sil.
        editor.clear();
        editor.commit();

        // çıkıştan sonra giriş ekranına yönlendir.
        Intent i = new Intent(_context, LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    /**
     * Giriş için hızlı kontrol.
     * **/
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
