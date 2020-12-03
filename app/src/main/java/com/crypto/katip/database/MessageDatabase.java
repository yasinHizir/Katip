package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crypto.katip.models.TextMessage;

import java.util.ArrayList;
import java.util.Date;

public class MessageDatabase extends Database{
    private static final String TABLE_NAME = "message";
    private static final String ID = "ID";
    private static final String CHAT_ID = "chatID";
    private static final String SELF = "self";
    private static final String BODY = "body";
    private static final String CREATED_AT = "created_at";

    private final int chatId;

    public MessageDatabase(DbHelper dbHelper, int chatId) {
        super(dbHelper);
        this.chatId = chatId;
    }

    public void save(String text, boolean self) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Date date = new Date();
        ContentValues values = new ContentValues();
        int selfValue = 0;
        if (self) {
            selfValue = 1;
        }

        values.put(CHAT_ID, chatId);
        values.put(SELF, selfValue);
        values.put(BODY, text);
        values.put(CREATED_AT, date.getTime());
        database.insert(TABLE_NAME, null, values);

        database.close();
    }

    public void remove(int id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + id);

        database.close();
    }

    public ArrayList<TextMessage> getMessages() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<TextMessage> messages = new ArrayList<>();

        try(Cursor cursor = database.rawQuery("SELECT " + ID + ", " + SELF + ", "+ BODY + " FROM " + TABLE_NAME + " WHERE " + CHAT_ID + " = " + chatId, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(BODY));
                    boolean self = false;
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(SELF)) == 1) {
                        self = true;
                    }
                    messages.add(new TextMessage(messageId, self, chatId, body, this));
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return messages;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + CHAT_ID + " INTEGER NOT NULL, " + SELF + " INTEGER, " + BODY + " TEXT, " + CREATED_AT + " INTEGER, FOREIGN KEY(" + CHAT_ID + ") REFERENCES " + ChatDatabase.getTableName() + " (ID));";
    }

    public static String getDropTable() {
        return "DROP TABLE EXISTS " + TABLE_NAME;
    }
}
