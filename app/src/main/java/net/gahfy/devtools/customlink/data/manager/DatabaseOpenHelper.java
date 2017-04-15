package net.gahfy.devtools.customlink.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.gahfy.devtools.customlink.R;

/**
 * This class is the application's implementation of DatabaseOpenHelper, which manages database
 * creation and version management.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    /**
     * The current version of the database of the application
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * The name of the database of the application
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATABASE_NAME = "net.gahfy.devtools";

    /**
     * Context in which the application is running
     */
    private Context mContext;

    /**
     * <p>Constructor for DatabaseOpenHelper.</p>
     * <p>You should not use this class directly, but prefer DevToolsApplication.getDatabase()
     * instead, in order to take advantage of Singleton architecture.</p>
     * @param context Context in which the application is running.
     */
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // We execute all the queries in create_database string array
        String[] createQueries = mContext.getResources().getStringArray(R.array.create_database);
        for(String currentQuery:createQueries) {
            db.execSQL(currentQuery);
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
