package com.crypto.katip.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class to manage the application database.
 * This class takes care of opening the database if it exists,
 * creating it if it does not, and upgrading it as necessary.
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, Database.getDatabaseName(), null, Database.getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create tables:
        sqLiteDatabase.execSQL(UserDatabase.getCreateTable());
        sqLiteDatabase.execSQL(PreKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(SignedPreKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(IdentityKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(SessionDatabase.getCreateTable());
        sqLiteDatabase.execSQL(ChatDatabase.getCreateTable());
        sqLiteDatabase.execSQL(MessageDatabase.getCreateTable());
        sqLiteDatabase.execSQL(KeyBundleDatabase.getCreateTable());

        // Create triggers:
        sqLiteDatabase.execSQL(Database.getRemoveMessagesTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveChatsTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveIdentityKeysTrigger());
        sqLiteDatabase.execSQL(Database.getRemovePreKeysTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveSessionsTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveSignedPreKeysTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveKeyBundleTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveKeyBundlesTrigger());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop tables:
        sqLiteDatabase.execSQL(UserDatabase.getDropTable());
        sqLiteDatabase.execSQL(PreKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(SignedPreKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(IdentityKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(SessionDatabase.getDropTable());
        sqLiteDatabase.execSQL(ChatDatabase.getDropTable());
        sqLiteDatabase.execSQL(MessageDatabase.getDropTable());
        sqLiteDatabase.execSQL(KeyBundleDatabase.getDropTable());

        onCreate(sqLiteDatabase);
    }
}