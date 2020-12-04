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

public class SignedPreKeyDatabase extends Database{
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

    public SignedPreKeyRecord load(int keyId){
        SignedPreKeyRecord record = null;
        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {
            String query = "SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + KEY_ID + " = " + keyId;
            try (Cursor cursor = database.rawQuery(query, null)) {

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

    public List<SignedPreKeyRecord> loadAll(){
        List<SignedPreKeyRecord> records = new LinkedList<>();
        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String query = "SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId;
            try (Cursor cursor = database.rawQuery(query , null)) {
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

    public void store(int keyId, SignedPreKeyRecord record){
        byte[] publicKey = record.getKeyPair().getPublicKey().serialize();
        byte[] privateKey = record.getKeyPair().getPrivateKey().serialize();
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(KEY_ID, keyId);
            values.put(PUBLIC_KEY, Base64.encode(publicKey, Base64.DEFAULT));
            values.put(PRIVATE_KEY, Base64.encode(privateKey, Base64.DEFAULT));
            values.put(SIGNATURE, record.getSignature());
            values.put(TIMESTAMP, record.getTimestamp());
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

    private SignedPreKeyRecord create(int keyId, Cursor cursor) throws InvalidKeyException{
        byte[] publicKeyEncoded = cursor.getBlob(cursor.getColumnIndexOrThrow(PUBLIC_KEY));
        byte[] privateKeyEncoded = cursor.getBlob(cursor.getColumnIndexOrThrow(PRIVATE_KEY));
        byte[] signature = cursor.getBlob(cursor.getColumnIndexOrThrow(SIGNATURE));
        return new SignedPreKeyRecord(keyId, cursor.getInt(cursor.getColumnIndexOrThrow(TIMESTAMP)), new ECKeyPair(Curve.decodePoint(Base64.decode(publicKeyEncoded, Base64.DEFAULT), 0),Curve.decodePrivatePoint(Base64.decode(privateKeyEncoded, Base64.DEFAULT))), signature);
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + KEY_ID + " INTEGER, " + PUBLIC_KEY + " BLOB, " + PRIVATE_KEY + " BLOB, " + SIGNATURE + " BLOB, " + TIMESTAMP + " INTEGER, UNIQUE(" + USER_ID + "," + KEY_ID + "), FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (ID));";
    }

    public static String getDropTable(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
