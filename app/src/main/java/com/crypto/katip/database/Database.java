package com.crypto.katip.database;

/**
 *  General structure of the database
 */
public abstract class Database {
    private static final String DATABASE_NAME = "Katip.db";
    private final static int DATABASE_VERSION = 1;
    protected DbHelper dbHelper;

    public Database(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    /**
     * @return Created trigger that removing the messages when after delete the chat
     */
    public static String getRemoveMessagesTrigger() {
        return "CREATE TRIGGER remove_messages_after_delete_chat AFTER DELETE ON " + ChatDatabase.getTableName() + " BEGIN DELETE FROM " + MessageDatabase.getTableName() + " WHERE " + MessageDatabase.getChatId() + " = OLD." + ChatDatabase.getID() + "; END;";
    }

    /**
     * @return Created trigger that removing the chats when after delete the user
     */
    public static String getRemoveChatsTrigger() {
        return "CREATE TRIGGER remove_chats_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + ChatDatabase.getTableName() + " WHERE " + ChatDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    /**
     * @return Created trigger that removing the identity keys when after delete the user
     */
    public static String getRemoveIdentityKeysTrigger() {
        return "CREATE TRIGGER remove_identity_keys_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + IdentityKeyDatabase.getTableName() + " WHERE " + IdentityKeyDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    /**
     * @return Created trigger that removing the pre-keys when after delete the user
     */
    public static String getRemovePreKeysTrigger() {
        return "CREATE TRIGGER remove_pre_keys_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + PreKeyDatabase.getTableName() + " WHERE " + PreKeyDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    /**
     * @return Created trigger that removing the sessions when after delete the user
     */
    public static String getRemoveSessionsTrigger() {
        return "CREATE TRIGGER remove_sessions_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + SessionDatabase.getTableName() + " WHERE " + SessionDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    /**
     * @return Created trigger that removing the signed pre-keys when after delete the user
     */
    public static String getRemoveSignedPreKeysTrigger() {
        return "CREATE TRIGGER remove_signed_pre_keys_after_delete_user AFTER DELETE ON " + UserDatabase.getTableName() + " BEGIN DELETE FROM " + SignedPreKeyDatabase.getTableName() + " WHERE " + SignedPreKeyDatabase.getUserId() + " = OLD." + UserDatabase.getID() + "; END;";
    }

    /**
     * @return Created trigger that removing the key bundle when after delete the pre-key
     */
    public static String getRemoveKeyBundleTrigger() {
        return "CREATE TRIGGER remove_key_bundle_after_delete_pre_key AFTER DELETE ON " + PreKeyDatabase.getTableName() + " BEGIN DELETE FROM " + KeyBundleDatabase.getTableName() + "  WHERE " + KeyBundleDatabase.getUserId() + " = OLD." + PreKeyDatabase.getUserId() + " AND " + KeyBundleDatabase.getPreKeyId() + "= OLD." + PreKeyDatabase.getKeyId() + "; END;";
    }

    /**
     * @return Created trigger that removing the key bundles when after delete the signed pre-key
     */
    public static String getRemoveKeyBundlesTrigger() {
        return "CREATE TRIGGER remove_key_bundles_after_delete_signed_pre_Key AFTER DELETE ON " + SignedPreKeyDatabase.getTableName() +  " BEGIN DELETE FROM " + KeyBundleDatabase.getTableName() + "  WHERE " + KeyBundleDatabase.getUserId() + " = OLD." + SignedPreKeyDatabase.getUserId() + " AND " + KeyBundleDatabase.getSignedPreKeyId() + "= OLD." + SignedPreKeyDatabase.getKeyId() + "; END;";
    }
}