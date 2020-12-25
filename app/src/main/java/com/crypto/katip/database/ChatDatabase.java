package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.crypto.katip.database.models.Chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ChatDatabase extends Database {
    private static final String TABLE_NAME = "chat";
    private static final String ID = "ID";
    private static final String USER_ID = "userID";
    private static final String REMOTE_UUID = "remote_uuid";
    private static final String INTERLOCUTOR = "interlocutor";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final int userId;

    public ChatDatabase(DbHelper dbHelper, int userId) {
        super(dbHelper);
        this.userId = userId;
    }

    public void save(UUID uuid, String interlocutor) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()){
            Date date = new Date();
            ContentValues values = new ContentValues();

            values.put(REMOTE_UUID, uuid.toString());
            values.put(USER_ID, userId);
            values.put(INTERLOCUTOR, interlocutor);
            values.put(CREATED_AT, date.getTime());
            values.put(UPDATED_AT, date.getTime());
            database.insert(TABLE_NAME, null, values);
        }
    }

    public boolean isRegistered(String interlocutor) {
        boolean result = false;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()){
            String sql = "SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + INTERLOCUTOR + " = '" + interlocutor + "' AND " + USER_ID + " = " + userId;
            try (Cursor cursor = database.rawQuery(sql, null)){
                if (cursor != null && cursor.moveToFirst()) {
                    result = true;
                }
            }
        }

        return result;
    }

    public ArrayList<String> getChatNames() {
        ArrayList<String> interlocutor = new ArrayList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()){
            try (Cursor cursor = database.rawQuery("SELECT " + INTERLOCUTOR + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        interlocutor.add(cursor.getString((cursor.getColumnIndexOrThrow(INTERLOCUTOR))));
                    } while (cursor.moveToNext());
                }
            }
        }

        return interlocutor;
    }

    @Nullable
    public Chat getChat(String interlocutor) {
        Chat chat = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()){
            try (Cursor cursor = database.rawQuery("SELECT " + ID + ", " + REMOTE_UUID + " FROM " + TABLE_NAME + " WHERE " + INTERLOCUTOR + " = '" + interlocutor + "' AND " + USER_ID + " = " + userId, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    UUID remoteUUID = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(REMOTE_UUID)));
                    chat = new Chat(cursor.getInt(cursor.getColumnIndexOrThrow(ID)), userId, remoteUUID, interlocutor);
                }
            }
        }

        return chat;
    }

    public void remove(int id) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()){
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
            database.execSQL(sql);
        }
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + REMOTE_UUID + " TEXT UNIQUE, " + INTERLOCUTOR + " TEXT, " + CREATED_AT + " INTEGER, " + UPDATED_AT + " INTEGER, FOREIGN KEY(" + USER_ID + ") REFERENCES user (ID));";
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
}
