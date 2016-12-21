package hr.foi.air.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Marko on 20.12.2016..
 */

@Database(name = FoiDatabase.NAME, version = FoiDatabase.VERSION)
public class FoiDatabase {
    public static final String NAME = "foirun";
    public static final int VERSION = 1;
}
