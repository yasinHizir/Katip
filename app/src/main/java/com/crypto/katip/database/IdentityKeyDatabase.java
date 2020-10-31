package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;

public class IdentityKeyDatabase extends Database{
    private static final String TABLE_NAME = "identity_key";
    private static final String ID = "ID";
    private static final String USER_ID = "userID";
    private static final String ADDRESS = "address";
    private static final String KEY = "key";

    private final int userId;

    public IdentityKeyDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    public IdentityKey get(String address){
        IdentityKey identityKey = null;
        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String query = "SELECT " + KEY + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + ADDRESS + " = '" + address + "';";
            try (Cursor cursor = database.rawQuery(query, null)) {

                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        identityKey = new IdentityKey(Curve.decodePoint(cursor.getBlob(cursor.getColumnIndexOrThrow(KEY)), 0));
                    }
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }

        return identityKey;
    }

    public boolean save(String address, IdentityKey identityKey){
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(ADDRESS, address);
            values.put(KEY, identityKey.serialize());
            database.insert(TABLE_NAME, null, values);
        }

        return get(address) != null;
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + ADDRESS + " TEXT, " + KEY + " BLOB, FOREIGN KEY(" + USER_ID + ") REFERENCES user (ID));";
    }

    public static String getDropTable(){
        return  "DROP TABLE " + TABLE_NAME;
    }
}