package com.crypto.katip.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.SignalStore;
import com.crypto.katip.database.models.User;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.util.KeyHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * This class manages to store {@link User}.
 */
public class UserDatabase extends Database {
    private static final String TABLE_NAME = "user";
    private static final String UUID = "uuid";
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

    public void save(String username, String password) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
            Date date = new Date();
            ContentValues values = new ContentValues();

            values.put(UUID, java.util.UUID.randomUUID().toString());
            values.put(USERNAME, username);
            values.put(PASSWORD, passwordDigest(password));
            values.put(REGISTRATION_ID, KeyHelper.generateRegistrationId(false));
            values.put(IDENTITY_PUBLIC_KEY, identityKeyPair.getPublicKey().serialize());
            values.put(IDENTITY_PRIVATE_KEY, identityKeyPair.getPrivateKey().serialize());
            values.put(CREATED_AT, date.getTime());
            values.put(UPDATED_AT, date.getTime());
            database.insert(TABLE_NAME, null, values);
        }
    }

    /**
     * This method receives the user to given the username.
     *
     * @param username  Username
     * @return          Returns User
     */
    @Nullable
    public User get(String username, Context context) {
        User user = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + ID + ", " + UUID +
                    " FROM " + TABLE_NAME +
                    " WHERE " + USERNAME + " = '" + username + "'";

            try (Cursor cursor = database.rawQuery(sql, null)){
                if (cursor != null && cursor.moveToFirst()) {
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                    user = new User(
                            java.util.UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(UUID))),
                            userId,
                            username,
                            new SignalStore(
                                    userId,
                                    context
                            )
                    );
                }
            }
        }

        return user;
    }

    /**
     * This method receives the user to given the id.
     *
     * @param id    The user id
     * @return      Returns the user
     */
    @Nullable
    public User get(int id, Context context) {
        User user = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + UUID + ", " + USERNAME +
                    " FROM " + TABLE_NAME +
                    " WHERE " + ID + " = " + id;

            try (Cursor cursor = database.rawQuery(sql, null)) {

                if (cursor != null && cursor.moveToFirst()) {

                    user = new User(
                            java.util.UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(UUID))),
                            id,
                            cursor.getString(cursor.getColumnIndexOrThrow(USERNAME)),
                            new SignalStore(
                                    id,
                                    context
                            )
                    );
                }
            }
        }

        return user;
    }

    /**
     * This method update the username or the password to given the id.
     *
     * @param id    The user id
     */
    public void update(int id, String username, String password) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {

            String sql =
                    "Update " + TABLE_NAME + " SET "
                    + USERNAME + " = '" + username + "' , "
                    + PASSWORD + " = '" + passwordDigest(password) + "' ,"
                    + UPDATED_AT + " = " + new Date().getTime() +
                    " WHERE " + ID + " = " + id + ";";

            database.execSQL(sql);
        }
    }

    /**
     * This method removes the user to given the id
     *
     * @param id    The user id
     */
    public void remove(int id) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {

            String sql =
                    "DELETE FROM " + TABLE_NAME +
                    " WHERE " + ID + " = " + id;

            database.execSQL(sql);
        }
    }

    /**
     * Is the user registered in the database?
     *
     * @param username  Username
     * @param password  User password
     * @return          Registered or not registered
     */
    public boolean isRegistered(String username, String password) {
        boolean result = false;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + PASSWORD +
                            " FROM " + TABLE_NAME +
                            " WHERE " + USERNAME + " = '" + username + "'";

            try (Cursor cursor = database.rawQuery(sql, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    String passwordRegistered = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
                    result = passwordRegistered.equals(passwordDigest(password));
                }
            }
        }

        return result;
    }

    /**
     * The method receives the identity key pair of the user
     *
     * @param id    User id
     * @return      The identity key pair of the user
     */
    public IdentityKeyPair getIdentityKeyPair(int id) {
        IdentityKeyPair identityKeyPair = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + IDENTITY_PUBLIC_KEY + ", " + IDENTITY_PRIVATE_KEY +
                    " FROM " + TABLE_NAME +
                    " WHERE " + ID + " = " + id;

            try (Cursor cursor = database.rawQuery(sql, null)) {

                if (cursor != null && cursor.moveToFirst()) {

                    try {
                        identityKeyPair = new IdentityKeyPair(
                                new IdentityKey(
                                        Curve.decodePoint(
                                                cursor.getBlob(cursor.getColumnIndexOrThrow(IDENTITY_PUBLIC_KEY)),
                                                0
                                        )
                                ),
                                Curve.decodePrivatePoint(cursor.getBlob(cursor.getColumnIndexOrThrow(IDENTITY_PRIVATE_KEY)))
                        );
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return identityKeyPair;
    }

    /**
     * The method receives the registration id of the user
     *
     * @param id    User id
     * @return      The registration id of the user
     */
    public int getRegistrationID(int id) {
        int registrationID = -1;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + REGISTRATION_ID +
                    " FROM " + TABLE_NAME +
                    " WHERE " + ID + " = " + id;

            try (Cursor cursor = database.rawQuery(sql, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    registrationID = cursor.getInt(cursor.getColumnIndexOrThrow(REGISTRATION_ID));
                }
            }
        }

        return registrationID;
    }

    private String passwordDigest(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());

            return new String(messageDigest.digest());
        } catch (NoSuchAlgorithmException | IllegalArgumentException e ) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + ID + " INTEGER PRIMARY KEY, "
                + UUID + " TEXT UNIQUE, "
                + USERNAME + " TEXT UNIQUE, "
                + PASSWORD + " TEXT, "
                + IDENTITY_PUBLIC_KEY + " BLOB, "
                + IDENTITY_PRIVATE_KEY + " BLOB, "
                + REGISTRATION_ID + " INTEGER, "
                + CREATED_AT + " INTEGER, "
                + UPDATED_AT + " INTEGER"
                + " );";
    }

    public static String getDropTable() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getID() {
        return ID;
    }
}