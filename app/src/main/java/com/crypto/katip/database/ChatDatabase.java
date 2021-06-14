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

    public void save(UUID remoteUUID, String interlocutor) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()){
            Date date = new Date();
            ContentValues values = new ContentValues();

            values.put(REMOTE_UUID, remoteUUID.toString());
            values.put(USER_ID, userId);
            values.put(INTERLOCUTOR, interlocutor);
            values.put(CREATED_AT, date.getTime());
            values.put(UPDATED_AT, date.getTime());
            database.insert(TABLE_NAME, null, values);
        }
    }

    public void remove(int id) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()){
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
            database.execSQL(sql);
        }
    }

    public void update(int id, UUID remoteUUID, String interlocutor) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()){
            Date date = new Date();

            String sql = "Update " + TABLE_NAME + " SET " + REMOTE_UUID + " = '" + remoteUUID.toString() + "' , " + INTERLOCUTOR + " = '" + interlocutor + "' ," + UPDATED_AT + " = " + date.getTime() + " WHERE " + ID + " = " + id + ";";
            database.execSQL(sql);
        }
    }

    public boolean isRegistered(UUID remoteUUID) {
        return getChat(remoteUUID) != null;
    }

    public ArrayList<Chat> getChats() {
        ArrayList<Chat> chats = new ArrayList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()){
            try (Cursor cursor = database.rawQuery("SELECT " + ID + ", " + REMOTE_UUID + ", " + INTERLOCUTOR + " FROM " + TABLE_NAME + " WHERE " + USER_ID + " = " + userId, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                        UUID remoteUUID = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(REMOTE_UUID)));
                        String interlocutor = cursor.getString(cursor.getColumnIndexOrThrow(INTERLOCUTOR));
                        chats.add(new Chat(id, userId, remoteUUID, interlocutor));
                    } while (cursor.moveToNext());
                }
            }
        }

        return chats;
    }

    @Nullable
    public Chat getChat(UUID remoteUUID) {
        Chat chat = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {
            try (Cursor cursor = database.rawQuery("SELECT " + ID + ", " + INTERLOCUTOR + " FROM " + TABLE_NAME + " WHERE " + REMOTE_UUID + " = '" + remoteUUID.toString() + "' AND " + USER_ID + " = " + userId, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                    String interlocutor = cursor.getString(cursor.getColumnIndexOrThrow(INTERLOCUTOR));
                    chat = new Chat(id, userId, remoteUUID, interlocutor);
                }
            }
        }

        return chat;
    }

    @Nullable
    public Chat getChat(int chatID) {
        Chat chat = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {
            try (Cursor cursor = database.rawQuery("SELECT " + INTERLOCUTOR + ", " + REMOTE_UUID + " FROM " + TABLE_NAME + " WHERE " + ID + " = " + chatID + " AND " + USER_ID + " = " + userId, null)){
                if (cursor != null && cursor.moveToFirst()) {
                    UUID remoteUUID = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(REMOTE_UUID)));
                    chat = new Chat(chatID, userId, remoteUUID, cursor.getString(cursor.getColumnIndexOrThrow(INTERLOCUTOR)));
                }
            }
        }

        return chat;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + USER_ID + " INTEGER NOT NULL, " + REMOTE_UUID + " TEXT, " + INTERLOCUTOR + " TEXT, " + CREATED_AT + " INTEGER, " + UPDATED_AT + " INTEGER, FOREIGN KEY(" + USER_ID + ") REFERENCES user (ID));";
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
