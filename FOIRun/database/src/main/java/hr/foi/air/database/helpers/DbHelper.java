package hr.foi.air.database.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air.database.core.DbModels;
import hr.foi.air.database.core.SuperEntity;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = 30;
    private static final String DBNAME = "foirun.db";

    private static final String CREATE_TABLE_LOCATION = "create table "
            + DbModels.LOCATION.TABLE + " ( "
            + (DbModels.PRIMARY_KEY +" integer primary key autoincrement, ")
            + (DbModels.LOCATION.ACTIVITY_ID + " integer not null, ")
            + (DbModels.LOCATION.LAP + " integer not null, ")
            + (DbModels.LOCATION.TYPE + " integer not null, ")
            + (DbModels.LOCATION.TIME + " integer not null, ")
            + (DbModels.LOCATION.LONGITUDE + " real not null, ")
            + (DbModels.LOCATION.LATITUDE + " real not null, ")
            + (DbModels.LOCATION.ACCURANCY + " real, ")
            + (DbModels.LOCATION.ALTITUDE + " real, ")
            + (DbModels.LOCATION.SPEED + " real, ")
            + (DbModels.LOCATION.BEARING + " real, ")
            + (DbModels.LOCATION.HR + " integer, ")
            + (DbModels.LOCATION.CADENCE + " integer ")
            + ");";

    private static final String CREATE_TABLE_USER =" create table "
            + DbModels.LOGIN.TABLE + " ("
            + (DbModels.LOGIN.EMAIL + " varchar(30) primary key, ")
            + (DbModels.LOGIN.NAME + " varchar(30) not null, ")
            + (DbModels.LOGIN.ACCESS_TOKEN + " varchar(100)" )
            + " );";

    private static DbHelper singleton = null;

    private static synchronized DbHelper getHelper(Context context) {
        if (singleton == null) {
            singleton = new DbHelper(context.getApplicationContext(), 1);
        }
        return singleton;
    }

    @Override
    public synchronized void close() {
        if (singleton != null) {
            // don't close
            return;
        }
        super.close();
    }

    private static SQLiteDatabase sReadableDB = null;
    private static SQLiteDatabase sWritableDB = null;

    public static synchronized SQLiteDatabase getReadableDatabase(Context context) {
        if (sReadableDB == null) {
            sReadableDB =getHelper(context).getReadableDatabase();
        }
        return sReadableDB;
    }

    public static synchronized SQLiteDatabase getWritableDatabase(Context context) {
        if (sWritableDB == null) {
            sWritableDB =getHelper(context).getWritableDatabase();
        }
        return sWritableDB;
    }

    public static synchronized void closeDB(SQLiteDatabase db) {
    }

    private DbHelper(Context context, int a) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        arg0.execSQL(CREATE_TABLE_LOCATION);
        arg0.execSQL(CREATE_TABLE_USER);

        onUpgrade(arg0, 0, DBVERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {

        Log.e(getClass().getName(), "onUpgrade: oldVersion: " + oldVersion + ", newVersion: " + newVersion);

        if (newVersion < oldVersion) {
            throw new java.lang.UnsupportedOperationException(
                    "Downgrade not supported");
        }

    }

    private static void echoDo(SQLiteDatabase arg0, String str) {

        Log.e("DBHelper", "execSQL(" + str + ")");
        arg0.execSQL(str);

    }

    public static ContentValues get(Cursor c) {
        if (c.isClosed() || c.isAfterLast() || c.isBeforeFirst())
            return null;
        ContentValues ret = new ContentValues();
        final int cnt = c.getColumnCount();
        for (int i = 0; i < cnt; i++) {
            if (!c.isNull(i)) {
                ret.put(c.getColumnName(i), c.getString(i));
            }
        }
        return ret;
    }

    public static ContentValues[] toArray(Cursor c) {
        ArrayList<ContentValues> list = new ArrayList<ContentValues>();
        if (c.moveToFirst()) {
            do {
                list.add(get(c));
            } while (c.moveToNext());
        }
        return list.toArray(new ContentValues[list.size()]);
    }

    public static int bulkInsert(List<? extends SuperEntity> objectList, SQLiteDatabase db) {
        int result = 0;
        for (SuperEntity obj : objectList) {
            long id = obj.insert(db);
            if (id != -1) {
                result++;
            }
        }
        return result;
    }
}
