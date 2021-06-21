package com.crypto.katip.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.SessionRecord;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class manages to store {@link org.whispersystems.libsignal.state.SessionRecord}.
 */
public class SessionDatabase extends Database {
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

    /**
     * This method saves the session to the database
     *
     * @param address   The signal protocol address of the remote user
     * @param record    Session record
     */
    public void save(SignalProtocolAddress address, SessionRecord record) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USER_ID, userId);
            values.put(DEVICE_ID, address.getDeviceId());
            values.put(NAME, address.getName());
            values.put(RECORD, record.serialize());
            database.insert(TABLE_NAME, null, values);
        }
    }

    /**
     * This method gives the session registered in the database
     *
     * @param address   The signal protocol address of the remote user
     * @return          Session record
     */
    public SessionRecord get(SignalProtocolAddress address) {
        SessionRecord record = null;

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + RECORD + " FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId +
                    " AND " + DEVICE_ID + " = " + address.getDeviceId() +
                    " AND " + NAME + " = '" + address.getName() + "';";

            try (Cursor cursor = database.rawQuery(sql, null)) {
                try {
                    if (cursor != null && cursor.moveToLast()) {
                        record = new SessionRecord(cursor.getBlob(cursor.getColumnIndexOrThrow(RECORD)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return record;
    }

    /**
     * This method implement getSubDeviceSessions() method situated
     * {@link org.whispersystems.libsignal.state.SessionStore}
     *
     * @param name  The name of the client.
     * @return      All known sub-devices with active sessions.
     */
    public List<Integer> getSubDevices(String name) {
        List<Integer> integers = new LinkedList<>();

        try (SQLiteDatabase database = dbHelper.getReadableDatabase()) {

            String sql =
                    "SELECT " + DEVICE_ID + " FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId +
                    " AND " + NAME + " = '" + name + "';";

            try (Cursor cursor = database.rawQuery(sql, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        integers.add(cursor.getInt(cursor.getColumnIndexOrThrow(DEVICE_ID)));
                    } while (cursor.moveToNext());
                }
            }
        }

        return integers;
    }

    /**
     * This method remove the session to given the signal protocol
     * address
     *
     * @param address   The signal protocol address of the remote user
     */
    public void remove(SignalProtocolAddress address) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {

            String sql =
                    "DELETE FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId +
                    " AND " + DEVICE_ID + " = " + address.getDeviceId() +
                    " AND " + NAME + " = '" + address.getName() + "';";

            database.execSQL(sql);
        }
    }

    /**
     * This method remove the all {@link org.whispersystems.libsignal.state.SessionRecord}s
     * corresponding to all devices to have the name
     *
     * @param name  The name of the client.
     */
    public void removeAll(String name) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {

            String sql =
                    "DELETE FROM " + TABLE_NAME +
                    " WHERE " + USER_ID + " = " + userId +
                    " AND " + NAME + " = '" + name + "';";

            database.execSQL(sql);
        }
    }

    /**
     * Do the remote user have its session in the database?
     *
     * @param address   The signal protocol address of the remote user
     * @return          Registered or not registered
     */
    public boolean contain(SignalProtocolAddress address) {
        return get(address) != null;
    }

    public static String getCreateTable() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + ID + " INTEGER PRIMARY KEY, "
                + USER_ID + " INTEGER NOT NULL, "
                + DEVICE_ID + " INTEGER, "
                + NAME + " TEXT, "
                + RECORD + " BLOB, "
                + "FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserDatabase.getTableName() + " (" + UserDatabase.getID() + ")"
                + ");";
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
}