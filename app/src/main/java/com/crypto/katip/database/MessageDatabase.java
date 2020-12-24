package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crypto.katip.database.models.TextMessage;

import java.util.ArrayList;
import java.util.Date;

public class MessageDatabase extends Database{
    private static final String TABLE_NAME = "message";
    private static final String ID = "ID";
    private static final String CHAT_ID = "chatID";
    private static final String OWN = "own";
    private static final String BODY = "body";
    private static final String CREATED_AT = "created_at";

    private final int chatId;

    public MessageDatabase(DbHelper dbHelper, int chatId) {
        super(dbHelper);
        this.chatId = chatId;
    }

    public void save(String text, boolean own) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()){
            Date date = new Date();
            ContentValues values = new ContentValues();

            int selfValue;
            if (own) {
                selfValue = 1;
            } else {
                selfValue = 0;
            }

            values.put(CHAT_ID, chatId);
            values.put(OWN, selfValue);
            values.put(BODY, text);
            values.put(CREATED_AT, date.getTime());
            database.insert(TABLE_NAME, null, values);
        }
    }

    public ArrayList<TextMessage> getMessages() {
        ArrayList<TextMessage> messages = new ArrayList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()){
            String sql = "SELECT " + ID + ", " + OWN + ", "+ BODY + " FROM " + TABLE_NAME + " WHERE " + CHAT_ID + " = " + chatId;
            try(Cursor cursor = database.rawQuery(sql, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                        String body = cursor.getString(cursor.getColumnIndexOrThrow(BODY));
                        boolean self = false;
                        if (cursor.getInt(cursor.getColumnIndexOrThrow(OWN)) == 1) {
                            self = true;
                        }
                        messages.add(new TextMessage(messageId, self, chatId, body));
                    } while (cursor.moveToNext());
                }
            }
        }

        return messages;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + CHAT_ID + " INTEGER NOT NULL, " + OWN + " INTEGER, " + BODY + " TEXT, " + CREATED_AT + " INTEGER, FOREIGN KEY(" + CHAT_ID + ") REFERENCES " + ChatDatabase.getTableName() + " (ID));";
    }

    public static String getDropTable() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getChatId() {
        return CHAT_ID;
    }
}
