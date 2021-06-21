package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.crypto.katip.database.models.Chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This class manages to store {@link Chat}.
 */
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

    /**
     * This method saves the chat to the database
     *
     * @param remoteUUID    Interlocutor uuid
     * @param interlocutor  Interlocutor name
     */
    public void save(UUID remoteUUID, String interlocutor) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
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

    /**
     * This method receives the all chats which user have.
     *
     * @return  returns List<Chat>
     */
    public List<Chat> get() {
        List<Chat> chats = new ArrayList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + ID + ", " + REMOTE_UUID + ", " + INTERLOCUTOR +
                    " FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId;

            try (Cursor cursor = database.rawQuery(sql, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        chats.add(new Chat(
                                        cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                                        userId,
                                        UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(REMOTE_UUID))),
                                        cursor.getString(cursor.getColumnIndexOrThrow(INTERLOCUTOR))
                                )
                        );
                    } while (cursor.moveToNext());
                }
            }
        }

        return chats;
    }

    /**
     * This method receives the chat to given the uuid.
     *
     * @param remoteUUID    interlocutor uuid
     * @return              returns the chat
     */
    @Nullable
    public Chat get(UUID remoteUUID) {
        Chat chat = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + ID + ", " + INTERLOCUTOR +
                    " FROM " + TABLE_NAME +
                    " WHERE " + REMOTE_UUID + " = '" + remoteUUID.toString() +
                    "' AND " + USER_ID + " = " + userId;

            try (Cursor cursor = database.rawQuery(sql, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    chat = new Chat(
                            cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                            userId,
                            remoteUUID,
                            cursor.getString(cursor.getColumnIndexOrThrow(INTERLOCUTOR))
                    );
                }
            }
        }

        return chat;
    }

    /**
     * This method receives the chat to given the chat id.
     *
     * @param chatId    the id of the requested chat
     * @return          returns the chat
     */
    @Nullable
    public Chat get(int chatId) {
        Chat chat = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + INTERLOCUTOR + ", " + REMOTE_UUID +
                    " FROM " + TABLE_NAME +
                    " WHERE " + ID + " = " + chatId +
                    " AND " + USER_ID + " = " + userId;

            try (Cursor cursor = database.rawQuery(sql, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    chat = new Chat(
                            chatId,
                            userId,
                            UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(REMOTE_UUID))),
                            cursor.getString(cursor.getColumnIndexOrThrow(INTERLOCUTOR))
                    );
                }
            }
        }

        return chat;
    }

    /**
     * This method remove the chat to given the chat id.
     *
     * @param id    the id of the chat
     */
    public void remove(int id) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {

            String sql =
                    "DELETE FROM " + TABLE_NAME +
                    " WHERE " + ID + " = " + id;

            database.execSQL(sql);
        }
    }

    /**
     * This method update the chat to given the chat id.
     *
     * @param id            the id of the chat
     * @param interlocutor  changed username of the interlocutor
     */
    public void update(int id, String interlocutor) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            Date date = new Date();

            String sql =
                    "UPDATE " + TABLE_NAME + " SET "
                    + INTERLOCUTOR + " = '" + interlocutor + "' ,"
                    + UPDATED_AT + " = " + date.getTime() +
                    " WHERE " + ID + " = " + id + ";";

            database.execSQL(sql);
        }
    }

    /**
     * Is the chat registered in the database?
     *
     * @param remoteUUID    interlocutor uuid
     * @return              registered or not
     *                      registered
     */
    public boolean isRegistered(UUID remoteUUID) {
        return get(remoteUUID) != null;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + ID + " INTEGER PRIMARY KEY, "
                + USER_ID + " INTEGER NOT NULL, "
                + REMOTE_UUID + " TEXT, "
                + INTERLOCUTOR + " TEXT, "
                + CREATED_AT + " INTEGER, "
                + UPDATED_AT + " INTEGER, "
                + "FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (" + UserDatabase.getID() + ")"
                + ");";
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