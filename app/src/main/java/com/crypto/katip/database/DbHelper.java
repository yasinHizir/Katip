package com.crypto.katip.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, Database.getDatabaseName(), null, Database.getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserDatabase.getCreateTable());
        sqLiteDatabase.execSQL(PreKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(SignedPreKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(IdentityKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(SessionDatabase.getCreateTable());
        sqLiteDatabase.execSQL(ChatDatabase.getCreateTable());
        sqLiteDatabase.execSQL(MessageDatabase.getCreateTable());

        sqLiteDatabase.execSQL(Database.getRemoveMessagesTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveChatsTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveIdentityKeysTrigger());
        sqLiteDatabase.execSQL(Database.getRemovePreKeysTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveSessionsTrigger());
        sqLiteDatabase.execSQL(Database.getRemoveSignedPreKeysTrigger());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(UserDatabase.getDropTable());
        sqLiteDatabase.execSQL(PreKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(SignedPreKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(IdentityKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(SessionDatabase.getDropTable());
        sqLiteDatabase.execSQL(ChatDatabase.getDropTable());
        sqLiteDatabase.execSQL(MessageDatabase.getDropTable());

        onCreate(sqLiteDatabase);
    }
}
