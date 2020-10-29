package com.crypto.katip.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(@Nullable Context context) {
        super(context, Database.getDatabaseName(), null, Database.getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserDatabase.getCreateTable());
        sqLiteDatabase.execSQL(PreKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(SignedPreKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(IdentityKeyDatabase.getCreateTable());
        sqLiteDatabase.execSQL(SessionDatabase.getCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(UserDatabase.getDropTable());
        sqLiteDatabase.execSQL(PreKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(SignedPreKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(IdentityKeyDatabase.getDropTable());
        sqLiteDatabase.execSQL(SessionDatabase.getDropTable());

        onCreate(sqLiteDatabase);
    }
}
