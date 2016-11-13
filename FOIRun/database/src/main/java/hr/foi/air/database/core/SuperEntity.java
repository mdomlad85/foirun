package hr.foi.air.database.core;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.Arrays;
import java.util.List;

public abstract class SuperEntity  implements DbEntity {

    private final ContentValues mContentValues;

    protected abstract List<String> getValidColumns();

    protected abstract String getTableName();

    protected abstract String getNullColumnHack();


    public SuperEntity() {
        this.mContentValues = new ContentValues();
    }

    /**
     * Returns the {@code ContentValues} wrapped by this object.
     */
    protected final ContentValues values() {
        return mContentValues;
    }

    public Long getId() {
        if (mContentValues.containsKey(DbModels.PRIMARY_KEY)) {
            return mContentValues.getAsLong(DbModels.PRIMARY_KEY);
        }
        return null;
    }

    public void setId(Long value) {
        values().put(DbModels.PRIMARY_KEY, value);
    }

    public long insert(SQLiteDatabase db) {
        this.setId(db.insert(getTableName(), getNullColumnHack(), values()));
        return this.getId();
    }

    public long update(SQLiteDatabase db) {
        if (getId() != null) {
            return db.update(getTableName(), values(), DbModels.PRIMARY_KEY + " = ?", new String[]{Long.toString(getId())});
        } else {
            throw new IllegalArgumentException("Entity has no primary key");
        }
    }

    protected void toContentValues(Cursor c) {
        if (c.isClosed() || c.isAfterLast() || c.isBeforeFirst()) {
            throw new CursorIndexOutOfBoundsException("Cursor not readable");
        }

        if (getValidColumns().containsAll(Arrays.asList(c.getColumnNames()))) {
            if (Build.VERSION.SDK_INT > 10) {
                this.cursorRowToContentValues(c, values());
            } else {
                DatabaseUtils.cursorRowToContentValues(c, values());
            }
        } else {
            throw new IllegalArgumentException("Cursor " + c.toString() + " is incompatible with the Entity " + this.getClass().getName());
        }

        for (String column : getValidColumns()) {
            if (values().get(column) == null)
                values().remove(column);
        }
    }

    // This is a replacement for DatabaseUtils.cursorRowToContentValues
    // see https://code.google.com/p/android/issues/detail?id=22219
    @SuppressLint("NewApi")
    private static void cursorRowToContentValues(Cursor cursor, ContentValues values) {
        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_NULL:
                    values.putNull(columns[i]);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    values.put(columns[i], cursor.getLong(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    values.put(columns[i], cursor.getDouble(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    values.put(columns[i], cursor.getString(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    values.put(columns[i], cursor.getBlob(i));
                    break;
            }
        }
    }

    public void readByPrimaryKey(SQLiteDatabase DB, long primaryKey) {
        String cols[] = new String[getValidColumns().size()];
        getValidColumns().toArray(cols);
        Cursor cursor = DB.query(getTableName(), cols, DbModels.PRIMARY_KEY+ " = "
                + primaryKey, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                toContentValues(cursor);
            }
        } finally {
            cursor.close();
        }
    }
}

