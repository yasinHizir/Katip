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

    private int userId;

    public SignedPreKeyDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    public SignedPreKeyRecord load(int keyId){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + KEY_ID + " = " + keyId, null);
        SignedPreKeyRecord record = null;

        try {
            if (cursor != null && cursor.moveToFirst()){
                create(keyId, cursor);
                cursor.close();
            }
        } catch (InvalidKeyException e){
            e.printStackTrace();
            cursor.close();
        } finally {
            database.close();
        }

        return record;
    }

    public List<SignedPreKeyRecord> loadAll(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + ", " + SIGNATURE + ", " + TIMESTAMP + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId, null);
        List<SignedPreKeyRecord> records = null;

        try {
            if (cursor != null && cursor.moveToFirst()){
                do {
                    records.add(create(userId, cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (InvalidKeyException e){
            e.printStackTrace();
            cursor.close();
        } finally {
            database.close();
        }

        return records;
    }

    public void store(int keyId, SignedPreKeyRecord record){
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

    public boolean contain(int keyId){
        return load(keyId) != null;
    }

    public void remove(int keyId){
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
