package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public PreKeyDatabase(DbHelper dbHelper) {
        super(dbHelper);
    }

    public PreKeyRecord load(int userId, int keyId){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PUBLIC_KEY + ", " + PRIVATE_KEY + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + keyId + " AND " + USER_ID + " = " + userId, null);
        PreKeyRecord record = null;

        if (cursor != null && cursor.moveToFirst()){
            try {
                byte[] publicKey = cursor.getBlob(cursor.getColumnIndexOrThrow(PUBLIC_KEY));
                byte[] privateKey = cursor.getBlob(cursor.getColumnIndexOrThrow(PRIVATE_KEY));
                record = new PreKeyRecord(keyId, new ECKeyPair(Curve.decodePoint(publicKey, 0),Curve.decodePrivatePoint(privateKey)));
            } catch (InvalidKeyException e){
                e.printStackTrace();
            } finally {
                cursor.close();
                database.close();
            }
        }

        return record;
    }

    public void store(int userId, int keyId, PreKeyRecord record){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_ID, userId);
        values.put(KEY_ID, keyId);
        values.put(PUBLIC_KEY, record.getKeyPair().getPublicKey().serialize());
        values.put(PRIVATE_KEY, record.getKeyPair().getPrivateKey().serialize());
        database.insert(TABLE_NAME, null, values);

        database.close();
    }

    public boolean contain(int userId, int keyId){
        return load(userId, keyId) != null;
    }

    public void remove(int userId, int keyId){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId +" AND " + KEY_ID + " = " + keyId);

        database.close();
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + KEY_ID + " INTEGER, " + PUBLIC_KEY + " BLOB, " + PRIVATE_KEY + " BLOB, UNIQUE(" + USER_ID + "," + KEY_ID + "), FOREIGN KEY(" + USER_ID + ") REFERENCES user (ID));";
    }

    public static String getDropTable(){
        return "DROP TABLE " + TABLE_NAME;
    }
}
