package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.crypto.katip.models.User;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECPrivateKey;
import org.whispersystems.libsignal.util.KeyHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class UserDatabase extends Database {
    private static final String TABLE_NAME = "user";
    private static final String ID = "ID";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String IDENTITY_PUBLIC_KEY = "identity_public_key";
    private static final String IDENTITY_PRIVATE_KEY = "identity_private_key";
    private static final String REGISTRATION_ID = "registration_id";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    public UserDatabase(DbHelper dbHelper) {
        super(dbHelper);
    }

    public void saveUser(String username, String password) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, passwordDigest(password));
        values.put(REGISTRATION_ID, KeyHelper.generateRegistrationId(false));
        values.put(IDENTITY_PUBLIC_KEY, identityKeyPair.getPublicKey().serialize());
        values.put(IDENTITY_PRIVATE_KEY, identityKeyPair.getPrivateKey().serialize());
        values.put(CREATED_AT, date.getTime());
        values.put(UPDATED_AT, date.getTime());
        database.insert(TABLE_NAME, null, values);

        database.close();
    }

    public void updateUser(int id, String username, String password) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Date date = new Date();

        String sql = "Update " + TABLE_NAME + " SET " + USERNAME + " = '" + username + "' , " + PASSWORD + " = '" + passwordDigest(password) + "' ," + UPDATED_AT + " = " + date.getTime() + " WHERE " + ID + " = " + id + ";";
        database.execSQL(sql);

        database.close();
    }

    public IdentityKeyPair getIdentityKeyPair(int id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + IDENTITY_PUBLIC_KEY + ", " + IDENTITY_PRIVATE_KEY + " FROM " + TABLE_NAME + " WHERE " + ID + " = " + id, null);
        IdentityKeyPair identityKeyPair = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                IdentityKey publicKey = new IdentityKey(Curve.decodePoint(cursor.getBlob(cursor.getColumnIndexOrThrow(IDENTITY_PUBLIC_KEY)), 0));
                ECPrivateKey privateKey = Curve.decodePrivatePoint(cursor.getBlob(cursor.getColumnIndexOrThrow(IDENTITY_PRIVATE_KEY)));
                identityKeyPair = new IdentityKeyPair(publicKey, privateKey);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        database.close();
        return identityKeyPair;
    }

    public int getRegistrationID(int id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + REGISTRATION_ID + " FROM " + TABLE_NAME + " WHERE " + ID + " = " + id, null);
        int registrationID = -1;

        if (cursor != null && cursor.moveToFirst()) {
            registrationID = cursor.getInt(cursor.getColumnIndexOrThrow(REGISTRATION_ID));
            cursor.close();
        }

        database.close();
        return registrationID;
    }

    @Nullable
    public User selectUser(String username) {
        User user = null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + USERNAME + " = '" + username + "'", null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(cursor.getInt(cursor.getColumnIndexOrThrow(ID)), username);
            cursor.close();
        }

        database.close();
        return user;
    }

    @Nullable
    public User findUser(int id) {
        User user = null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + USERNAME + " FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'", null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(id, cursor.getString(cursor.getColumnIndexOrThrow(USERNAME)));
            cursor.close();
        }

        database.close();
        return user;
    }

    public boolean isRegistered(String username, String password) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PASSWORD + " FROM " + TABLE_NAME + " WHERE " + USERNAME + " = '" + username + "'", null);

        boolean result = false;
        if (cursor != null && cursor.moveToFirst()) {
            String passwordRegistered = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
            cursor.close();
            result = passwordRegistered.equals(passwordDigest(password));
        }

        database.close();
        return result;
    }

    public void updateUsername(int id, String newUsername) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Date date = new Date();

        database.execSQL("UPDATE " + TABLE_NAME + " SET " + USERNAME + " = '" + newUsername + "', " + UPDATED_AT + " = " + date.getTime() + " WHERE " + ID + " = " + id);

        database.close();
    }

    public void updatePassword(int id, String newPassword) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Date date = new Date();

        database.execSQL("UPDATE " + TABLE_NAME + " SET " + PASSWORD + " = '" + passwordDigest(newPassword) + "', " + UPDATED_AT + " = " + date.getTime() + " WHERE " + ID + " = " + id);

        database.close();
    }


    public void removeUser(int id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + id);

        database.close();
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USERNAME + " TEXT UNIQUE, " + PASSWORD + " TEXT, " + IDENTITY_PUBLIC_KEY + " BLOB, " + IDENTITY_PRIVATE_KEY + " BLOB, " + REGISTRATION_ID + " INTEGER, " + CREATED_AT + " INTEGER, " + UPDATED_AT + " INTEGER );";
    }

    public static String getDropTable() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    private String passwordDigest(String password){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
        } catch (NoSuchAlgorithmException | IllegalArgumentException e ) {
            e.printStackTrace();
        }

        return messageDigest.toString();
    }
}
