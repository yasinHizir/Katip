package com.crypto.katip.database;

public abstract class Database {
    private static final String DATABASE_NAME = "katip.db";
    private static int DATABASE_VERSION = 1;
    protected DbHelper dbHelper;


    public Database(DbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public static String getDatabaseName(){
        return DATABASE_NAME;
    }

    public static int getDatabaseVersion(){
        return DATABASE_VERSION;
    }

    public static void setDatabaseVersion(int databaseVersion){
        DATABASE_VERSION = databaseVersion;
    }
}
