package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECPrivateKey;
import org.whispersystems.libsignal.util.KeyHelper;

public class UserDatabase extends Database{
    private static final String TABLE_NAME = "user";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String IDENTITY_PUBLIC_KEY = "identity_public_key";
    private static final String IDENTITY_PRIVATE_KEY = "identity_private_key";
    private static final String REGISTRATION_ID = "registration_id";

    public UserDatabase(DbHelper dbHelper) {
        super(dbHelper);
    }

    public IdentityKeyPair getIdentityKeyPair(String username) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[] {IDENTITY_PUBLIC_KEY, IDENTITY_PRIVATE_KEY}, USERNAME + "=?", new String[] {username},  null, null, null);
        IdentityKeyPair identityKeyPair = null;

        if (cursor != null){
            cursor.moveToFirst();
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
        return identityKeyPair;
    }

    public int getRegistrationID(String username){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[] {REGISTRATION_ID}, USERNAME + "=?",new String[]{username}, null, null, null);
        int registrationID = -1;

        if (cursor != null){
            cursor.moveToFirst();
            registrationID = cursor.getInt(cursor.getColumnIndexOrThrow(REGISTRATION_ID));
            cursor.close();
        }
        return registrationID;
    }

    public void save(String username, String password){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int passwordHash = password.hashCode();
        IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();

        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, passwordHash);
        values.put(REGISTRATION_ID, KeyHelper.generateRegistrationId(false));
        values.put(IDENTITY_PUBLIC_KEY, identityKeyPair.getPublicKey().serialize());
        values.put(IDENTITY_PRIVATE_KEY, identityKeyPair.getPrivateKey().serialize());
        database.insert(TABLE_NAME, null, values);
    }

    public boolean isRegistered(String username, String password){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int passwordHash = password.hashCode();
        Cursor cursor = database.query(TABLE_NAME, new String[]{PASSWORD}, USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            int passwordRegistered = cursor.getInt(cursor.getColumnIndexOrThrow(PASSWORD));
            cursor.close();
            return passwordRegistered == passwordHash;
        }
        return false;
    }

    public void remove(String username){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TABLE_NAME, USERNAME + "=?", new String[]{username});
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY, " + USERNAME + " TEXT UNIQUE, " + PASSWORD + " INTEGER, " + IDENTITY_PUBLIC_KEY + " BLOB," + IDENTITY_PRIVATE_KEY + " BLOB," + REGISTRATION_ID + " INTEGER " + ")";
    }

    public static String getDropTable(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
