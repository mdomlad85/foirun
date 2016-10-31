package hr.foi.air.database.core;

import android.database.sqlite.SQLiteDatabase;

/**
 * Every db entity must implement this methods
 */

interface DbEntity {
    long insert(SQLiteDatabase db);

    void update(SQLiteDatabase db);
}
