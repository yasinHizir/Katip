package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.SessionRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SessionDatabase extends Database{
    private static final String TABLE_NAME = "session";
    private static final String ID = "ID";
    private static final String USER_ID = "userID";
    private static final String DEVICE_ID = "deviceID";
    private static final String NAME = "name";
    private static final String RECORD = "record";

    private final int userId;

    public SessionDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    public SessionRecord load(SignalProtocolAddress address){
        SessionRecord record = null;
        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String query = "SELECT " + RECORD + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + DEVICE_ID + " = " + address.getDeviceId() + " AND " + NAME + " = '" + address.getName() + "';";
            try (Cursor cursor = database.rawQuery(query, null)) {

                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        record = new SessionRecord(cursor.getBlob(cursor.getColumnIndexOrThrow(RECORD)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return record;
    }

    public List<Integer> getSubDevices(String name){
        List<Integer> integers = new ArrayList<>();
        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String query = "SELECT " + DEVICE_ID + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + NAME + " = '" + name + "';";
            try (Cursor cursor = database.rawQuery(query, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        integers.add(cursor.getInt(cursor.getColumnIndexOrThrow(DEVICE_ID)));
                    } while (cursor.moveToNext());
                }
            }
        }

        return integers;
    }

    public void store(SignalProtocolAddress address, SessionRecord record){
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(DEVICE_ID, address.getDeviceId());
            values.put(NAME, address.getName());
            values.put(RECORD, record.serialize());
            database.insert(TABLE_NAME, null, values);
        }
    }

    public boolean contain(SignalProtocolAddress address){
        return load(address) != null;
    }

    public void delete(SignalProtocolAddress address){
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + DEVICE_ID + " = " + address.getDeviceId() + " AND " + NAME + " = '" + address.getName() + "';");
        }
    }

    public void deleteAll(String name){
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId + " AND " + NAME + " = '" + name + "';");
        }
    }

    public static String getCreateTable(){
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + DEVICE_ID + " INTEGER, " + NAME + " TEXT, " + RECORD + " BLOB, FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (ID));";
    }

    public static String getDropTable(){
        return "DROP TABLE " + TABLE_NAME;
    }
}
