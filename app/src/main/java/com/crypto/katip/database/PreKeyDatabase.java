package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.state.PreKeyRecord;

/**
 * This class manages to store {@link org.whispersystems.libsignal.state.PreKeyRecord}.
 */
public class PreKeyDatabase extends Database{
    private static final String TABLE_NAME = "pre_key";
    private static final String ID = "ID";
    private static final String USER_ID = "userID";
    private static final String KEY_ID = "keyID";
    private static final String PUBLIC_KEY = "public_key";
    private static final String PRIVATE_KEY = "private_key";

    private final int userId;

    public PreKeyDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    /**
     * This method saves the pre-key to the database
     *
     * @param keyId     Pre-key id
     * @param record    Pre-key record
     */
    public void save(int keyId, PreKeyRecord record) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            byte[] publicKey = Base64.encode(
                    record.getKeyPair().getPublicKey().serialize(),
                    Base64.DEFAULT
            );

            byte[] privateKey = Base64.encode(
                    record.getKeyPair().getPrivateKey().serialize(),
                    Base64.DEFAULT
            );

            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(KEY_ID, keyId);
            values.put(PUBLIC_KEY, publicKey);
            values.put(PRIVATE_KEY, privateKey);
            database.insert(TABLE_NAME, null, values);
        }
    }

    /**
     * This method receives the pre-key from the database
     *
     * @param keyId Pre-key id
     * @return      Pre-key record
     */
    public PreKeyRecord get(int keyId) {
        PreKeyRecord record = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY +
                    " FROM " + TABLE_NAME +
                    " WHERE " + KEY_ID + " = " + keyId +
                    " AND " + USER_ID + " = " + userId;

            try (Cursor cursor = database.rawQuery(sql, null)){
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        record = new PreKeyRecord(
                                keyId,
                                new ECKeyPair(
                                        Curve.decodePoint(
                                                Base64.decode(
                                                        cursor.getBlob(cursor.getColumnIndexOrThrow(PUBLIC_KEY)),
                                                        Base64.DEFAULT
                                                ),
                                                0
                                        ),
                                        Curve.decodePrivatePoint(
                                                Base64.decode(
                                                        cursor.getBlob(cursor.getColumnIndexOrThrow(PRIVATE_KEY)),
                                                        Base64.DEFAULT
                                                )
                                        )
                                )
                        );
                    }
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }

        return record;
    }

    /**
     * This method remove the pre-key to given the pre-key id
     *
     * @param keyId Pre-key id
     */
    public void remove(int keyId) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {

            String sql =
                    "DELETE FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId +
                    " AND " + KEY_ID + " = " + keyId;

            database.execSQL(sql);
        }
    }

    /**
     * Is the pre-key id registered in the database?
     *
     * @param keyId Pre-key id
     * @return      Registered or not registered
     */
    public boolean contain(int keyId) {
        return get(keyId) != null;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + ID + " INTEGER PRIMARY KEY, "
                + USER_ID + " INTEGER NOT NULL, "
                + KEY_ID + " INTEGER, "
                + PUBLIC_KEY + " BLOB, "
                + PRIVATE_KEY + " BLOB, "
                + "UNIQUE(" + USER_ID + "," + KEY_ID + "), "
                + "FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (" + UserDatabase.getID() + ")"
                + ");";
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

    public static String getUserId() {
        return USER_ID;
    }

    public static String getKeyId() {
        return KEY_ID;
    }
}