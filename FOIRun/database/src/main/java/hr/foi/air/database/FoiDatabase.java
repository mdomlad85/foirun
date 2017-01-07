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
    public static final int VERSION = 4;

    public static void FillActivityTracker() {

        String[] strTypes = {"Running", "Biking", "Walking", "Other"};

        List<ActivityType> types = ActivityType.getAll();

        if(types.size() == strTypes.length) return;

        for (ActivityType type : types) {
            for (String str: strTypes) {
                if(!str.equals(type.getName())){
                    new ActivityType(str).save();
                }
            }
        }
    }
}
