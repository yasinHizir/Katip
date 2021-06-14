package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

public class KeyBundleDatabase extends Database{
    private static final String TABLE_NAME = "key_bundle";
    private static final String USER_ID = "userID";
    private static final String SIGNED_PRE_KEY_ID = "singed_pre_key_id";
    private static final String PRE_KEY_ID = "pre_key_id";

    private final int userId;

    public KeyBundleDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    public void save(int signed_pre_key_id, int pre_key_id) {
        try(SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(SIGNED_PRE_KEY_ID, signed_pre_key_id);
            values.put(PRE_KEY_ID, pre_key_id);
            database.insert(TABLE_NAME, null, values);
        }
    }

    public void remove(int pre_key_id) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + PRE_KEY_ID + " = " + pre_key_id;
            database.execSQL(sql);
        }
    }

    public List<Integer> getPreKeys(int signed_pre_key_id) {
        List<Integer> integerList = new LinkedList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {
            String sql = "SELECT " + PRE_KEY_ID +  " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + SIGNED_PRE_KEY_ID + " = " + signed_pre_key_id;
            try (Cursor cursor = database.rawQuery(sql , null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Integer preKeyId = cursor.getInt(cursor.getColumnIndexOrThrow(PRE_KEY_ID));
                        integerList.add(preKeyId);
                    } while (cursor.moveToNext());
                }
            }
        }

        return integerList;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( " + USER_ID + " INTEGER NOT NULL, " + SIGNED_PRE_KEY_ID + " INTEGER NOT NULL, " + PRE_KEY_ID + " INTEGER NOT NULL, FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (ID), FOREIGN KEY(" + SIGNED_PRE_KEY_ID + ") REFERENCES " + SignedPreKeyDatabase.getTableName()+ "(ID), FOREIGN KEY(" + PRE_KEY_ID + ") REFERENCES " + PreKeyDatabase.getTableName()+ "(ID)" + ");";
    }

    public static String getDropTable() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getUserId() {
        return USER_ID;
    }

    public static String getPreKeyId() {
        return PRE_KEY_ID;
    }

    public static String getSignedPreKeyId() {
        return SIGNED_PRE_KEY_ID;
    }
}
