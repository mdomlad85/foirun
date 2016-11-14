package hr.foi.air.database.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hr.foi.air.database.core.DbModels;
import hr.foi.air.database.core.SuperEntity;

/**
 * Created by root on 11/14/16.
 */

public class LoginEntity extends SuperEntity {
    public LoginEntity() { super(); }
    
    private LoginEntity(Cursor c){
        super();
        toContentValues(c);
    }

    @Override
    protected List<String> getValidColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(DbModels.LOGIN.EMAIL);
        columns.add(DbModels.LOGIN.NAME);
        columns.add(DbModels.LOGIN.ACCESS_TOKEN);

        return columns;
    }

    public static class LoginList<E> implements Iterable<E> {
        LoginEntity.LoginList.LoginIterator iter;
        final String mEmail;
        final SQLiteDatabase mDB;

        public LoginList(SQLiteDatabase mDB, String mEmail) {
            this.mEmail = mEmail;
            this.mDB = mDB;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<E> iterator() {
            iter = new LoginEntity.LoginList.LoginIterator(this.mEmail, this.mDB);
            return iter;
        }

        public int getCount() {
            return iter == null ? 0 : iter.getCount();
        }

        public void close() {
            if (iter != null) {iter.close();}
        }

        private class LoginIterator implements Iterator<E> {
            private LoginIterator(String mEmail, SQLiteDatabase mDB) {
                c = mDB.query(DbModels.LOGIN.TABLE, from, DbModels.LOGIN.EMAIL + " == " + mEmail,
                        null, null, null, DbModels.PRIMARY_KEY, null);
                if (!c.moveToFirst()) {
                    c.close();
                }
            }

            final String[] from = new String[]{
                    DbModels.LOGIN.EMAIL,
                    DbModels.LOGIN.NAME,
                    DbModels.LOGIN.ACCESS_TOKEN,
            };
            Cursor c = null;
            E prev = null;

            public int getCount() {
                return c.getCount();
            }

            public void close() {
                if (!c.isClosed()) {
                    c.close();
                }
            }

            @Override
            public boolean hasNext() {
                return !c.isClosed() && !c.isLast();
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                c.moveToNext();
                prev = (E)new LoginEntity(c);
                if (c.isLast()) {
                    c.close();
                }
                return prev;
            }

            @Override
            public void remove() {
                next();
            }
        }
    }

    @Override
    protected String getTableName() {
        return DbModels.LOGIN.TABLE;
    }

    @Override
    protected String getNullColumnHack() {
        return null;
    }

    public void setLoginEmail(String value) {
        values().put(DbModels.LOGIN.EMAIL, value);
    }

    public String getLoginEmail() {
        if (values().containsKey(DbModels.LOGIN.EMAIL)) {
            return values().getAsString(DbModels.LOGIN.EMAIL);
        }
        return null;
    }

    public void setLoginName(String value) {
        values().put(DbModels.LOGIN.NAME, value);
    }

    public String getLoginName() {
        if (values().containsKey(DbModels.LOGIN.NAME)) {
            return values().getAsString(DbModels.LOGIN.NAME);
        }
        return null;
    }

    public void setLoginAccessToken(String value) {
        values().put(DbModels.LOGIN.ACCESS_TOKEN, value);
    }

    public String getLoginAccessToken() {
        if (values().containsKey(DbModels.LOGIN.ACCESS_TOKEN)) {
            return values().getAsString(DbModels.LOGIN.ACCESS_TOKEN);
        }
        return null;
    }
}
