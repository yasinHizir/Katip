package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.state.PreKeyRecord;

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

    public PreKeyRecord load(int keyId){
        PreKeyRecord record = null;
        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {
            Cursor cursor = database.rawQuery("SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + keyId + " AND " + USER_ID + " = " + userId, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    byte[] publicKey = cursor.getBlob(cursor.getColumnIndexOrThrow(PUBLIC_KEY));
                    byte[] privateKey = cursor.getBlob(cursor.getColumnIndexOrThrow(PRIVATE_KEY));
                    cursor.close();
                    record = new PreKeyRecord(keyId, new ECKeyPair(Curve.decodePoint(Base64.decode(publicKey, Base64.DEFAULT), 0), Curve.decodePrivatePoint(Base64.decode(privateKey, Base64.DEFAULT))));
                }
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        return record;
    }

    public void store(int keyId, PreKeyRecord record){
        byte[] publicKey = record.getKeyPair().getPublicKey().serialize();
        byte[] privateKey = record.getKeyPair().getPrivateKey().serialize();

        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(KEY_ID, keyId);
            values.put(PUBLIC_KEY, Base64.encode(publicKey, Base64.DEFAULT));
            values.put(PRIVATE_KEY, Base64.encode(privateKey, Base64.DEFAULT));
            database.insert(TABLE_NAME, null, values);
        }
    }

    public boolean contain(int keyId){
        return load(keyId) != null;
    }

    public void remove(int keyId){
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + KEY_ID + " = " + keyId);
        }
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + KEY_ID + " INTEGER, " + PUBLIC_KEY + " BLOB, " + PRIVATE_KEY + " BLOB, UNIQUE(" + USER_ID + "," + KEY_ID + "), FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (ID));";
    }

    public static String getDropTable(){
        return "DROP TABLE " + TABLE_NAME;
    }
}
