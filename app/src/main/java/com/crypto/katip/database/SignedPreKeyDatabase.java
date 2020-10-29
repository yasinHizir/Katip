package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

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

    public SignedPreKeyDatabase(DbHelper dbHelper) {
        super(dbHelper);
    }

    public SignedPreKeyRecord load(int userId, int keyId){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + KEY_ID + " = " + keyId, null);
        SignedPreKeyRecord record = null;

        if (cursor != null && cursor.moveToFirst()){
            try {
                create(keyId, cursor);

            } catch (InvalidKeyException e){
                e.printStackTrace();

            } finally {
                cursor.close();
                database.close();
            }
        }
        return record;
    }

    public List<SignedPreKeyRecord> loadAll(int userId){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId, null);
        List<SignedPreKeyRecord> records = null;

        if (cursor != null && cursor.moveToFirst()){
            try {
                do {
                    records.add(create(userId, cursor));
                } while (cursor.moveToNext());
            } catch (InvalidKeyException e){
                e.printStackTrace();
            } finally {
                cursor.close();
                database.close();
            }
        }

        return records;
    }

    public void store(int userId, int keyId, SignedPreKeyRecord record){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_ID, userId);
        values.put(KEY_ID, keyId);
        values.put(PUBLIC_KEY, record.getKeyPair().getPublicKey().serialize());
        values.put(PRIVATE_KEY, record.getKeyPair().getPrivateKey().serialize());
        values.put(SIGNATURE, record.getSignature());
        values.put(TIMESTAMP, record.getTimestamp());
        database.insert(TABLE_NAME, null, values);

        database.close();
    }

    public boolean contain(int userId, int keyId){
        return load(userId, keyId) != null;
    }

    public void remove(int userId, int keyId){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + KEY_ID + " = " + keyId);

        database.close();
    }

    private SignedPreKeyRecord create(int keyId, Cursor cursor) throws InvalidKeyException{
        byte[] publicKey = cursor.getBlob(cursor.getColumnIndexOrThrow(PUBLIC_KEY));
        byte[] privateKey = cursor.getBlob(cursor.getColumnIndexOrThrow(PRIVATE_KEY));
        byte[] signature = cursor.getBlob(cursor.getColumnIndexOrThrow(SIGNATURE));
        return new SignedPreKeyRecord(keyId, cursor.getInt(cursor.getColumnIndexOrThrow(TIMESTAMP)), new ECKeyPair(Curve.decodePoint(publicKey, 0),Curve.decodePrivatePoint(privateKey)), signature);
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + KEY_ID + " INTEGER, " + PUBLIC_KEY + " BLOB, " + PRIVATE_KEY + " BLOB, " + SIGNATURE + " BLOB, " + TIMESTAMP + " INTEGER, UNIQUE(" + USER_ID + "," + KEY_ID + "), FOREIGN KEY(" + USER_ID + ") REFERENCES user (ID));";
    }

    public static String getDropTable(){
        return "DROP TABLE " + TABLE_NAME;
    }
}