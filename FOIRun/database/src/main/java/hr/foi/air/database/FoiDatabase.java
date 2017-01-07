package hr.foi.air.database;

import com.raizlabs.android.dbflow.annotation.Database;

import java.util.List;

import hr.foi.air.database.entities.ActivityType;

/**
 * Created by Marko on 20.12.2016..
 */

@Database(name = FoiDatabase.NAME, version = FoiDatabase.VERSION)
public class FoiDatabase {
    public static final String NAME = "foirun";
    public static final int VERSION = 3;

    public static void FillActivityTracker() {

        String[] strTypes = {"Running", "Biking", "Walking", "Other"};

        List<ActivityType> types = ActivityType.getAll();

        for (ActivityType type : types) {
            type.delete();
        }

        for (String type : strTypes) {
           new ActivityType(type).save();
        }
    }
}
