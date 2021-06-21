package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.util.LinkedList;
import java.util.List;

/**
 * This class manages to store {@link org.whispersystems.libsignal.state.SignedPreKeyRecord}.
 */
public class SignedPreKeyDatabase extends Database {
    private static final String TABLE_NAME = "signed_pre_key";
    private static final String ID = "ID";
    private static final String USER_ID = "userID";
    private static final String KEY_ID = "keyID";
    private static final String PUBLIC_KEY = "public_key";
    private static final String PRIVATE_KEY = "private_key";
    private static final String SIGNATURE = "signature";
    private static final String TIMESTAMP = "timestamp";

    private final int userId;

    public SignedPreKeyDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    /**
     * This method saves the signed pre-key to the database.
     *
     * @param keyId     Signed pre-key id
     * @param record    Signed pre-key record
     */
    public void save(int keyId, SignedPreKeyRecord record) {
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
            values.put(SIGNATURE, record.getSignature());
            values.put(TIMESTAMP, record.getTimestamp());
            database.insert(TABLE_NAME, null, values);
        }
    }

    /**
     * This method receives the signed pre-key to given
     * signed pre-key id from the database
     *
     * @param keyId Signed pre-key id
     * @return      Signed pre-key record
     */
    public SignedPreKeyRecord get(int keyId) {
        SignedPreKeyRecord record = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP +
                    " FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId +
                    " AND " + KEY_ID + " = " + keyId;

            try (Cursor cursor = database.rawQuery(sql, null)) {
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        record = create(keyId, cursor);
                    }
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }

        return record;
    }

    /**
     * This method receives the all Signed pre-keys which user have.
     *
     * @return  List<SignedPreKeyRecord>
     */
    public List<SignedPreKeyRecord> get() {
        List<SignedPreKeyRecord> records = new LinkedList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP +
                    " FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId;

            try (Cursor cursor = database.rawQuery(sql , null)) {
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            records.add(create(userId, cursor));
                        } while (cursor.moveToNext());
                    }
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }

        return records;
    }

    /**
     * Is the signed pre-key registered in the database?
     *
     * @param keyId Signed pre-key id
     * @return      Registered or not registered
     */
    public boolean contain(int keyId) {
        return get(keyId) != null;
    }

    /**
     * This method remove the signed pre-key record to given
     * signed pre-key id.
     *
     * @param keyId Singed pre-key id
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

    private SignedPreKeyRecord create(int keyId, Cursor cursor) throws InvalidKeyException {
        return new SignedPreKeyRecord(
                keyId,
                cursor.getLong(cursor.getColumnIndexOrThrow(TIMESTAMP)),
                new ECKeyPair(
                        Curve.decodePoint(Base64.decode(cursor.getBlob(cursor.getColumnIndexOrThrow(PUBLIC_KEY)), Base64.DEFAULT), 0),
                        Curve.decodePrivatePoint(Base64.decode(cursor.getBlob(cursor.getColumnIndexOrThrow(PRIVATE_KEY)), Base64.DEFAULT))
                ),
                cursor.getBlob(cursor.getColumnIndexOrThrow(SIGNATURE))
        );
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + ID + " INTEGER PRIMARY KEY, "
                + USER_ID + " INTEGER NOT NULL, "
                + KEY_ID + " INTEGER, "
                + PUBLIC_KEY + " BLOB, "
                + PRIVATE_KEY + " BLOB, "
                + SIGNATURE + " BLOB, "
                + TIMESTAMP + " INTEGER, "
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