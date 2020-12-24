package com.crypto.katip.database;

public abstract class Database {
    private static final String DATABASE_NAME = "Katip.db";
    private final static int DATABASE_VERSION = 1;
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

    public static String getRemoveMessagesTrigger() {
        return "CREATE TRIGGER remove_messages_after_delete_chat AFTER DELETE ON " + ChatDatabase.getTableName() + " BEGIN DELETE FROM " + MessageDatabase.getTableName() + " WHERE " + MessageDatabase.getChatId() + " = OLD." + ChatDatabase.getID() + "; END;";
    }

    public static String getRemoveChatsTrigger() {
        return "CREATE TRIGGER remove_chats_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + ChatDatabase.getTableName() + " WHERE " + ChatDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    public static String getRemoveIdentityKeysTrigger() {
        return "CREATE TRIGGER remove_identity_keys_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + IdentityKeyDatabase.getTableName() + " WHERE " + IdentityKeyDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    public static String getRemovePreKeysTrigger() {
        return "CREATE TRIGGER remove_pre_keys_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + PreKeyDatabase.getTableName() + " WHERE " + PreKeyDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    public static String getRemoveSessionsTrigger() {
        return "CREATE TRIGGER remove_sessions_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + SessionDatabase.getTableName() + " WHERE " + SessionDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    public static String getRemoveSignedPreKeysTrigger() {
        return "CREATE TRIGGER remove_signed_pre_keys_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + SignedPreKeyDatabase.getTableName() + " WHERE " + SignedPreKeyDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }
}