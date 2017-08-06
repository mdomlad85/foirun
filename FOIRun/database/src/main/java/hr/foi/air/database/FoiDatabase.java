package hr.foi.air.database;

import com.raizlabs.android.dbflow.annotation.Database;

import java.util.List;

import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.database.entities.Aktivnost;

/**
 * Created by Marko on 20.12.2016..
 */

@Database(name = FoiDatabase.NAME, version = FoiDatabase.VERSION)
public class FoiDatabase {
    public static final String NAME = "foirun";
    public static final int VERSION = 5;

    public static void FillActivityTracker() {

        String[] strTypes = {"Running", "Biking", "Walking", "Other"};

        List<ActivityType> types = ActivityType.getAll();

        if(types.size() == strTypes.length) return;

        boolean exists = false;
        for (String str : strTypes) {
            for (ActivityType type: types) {
                if(str.equals(type.getName())){
                    exists = true;
                }
            }

            if(!exists){
                new ActivityType(str).save();
            }
            exists = false;
        }
    }

    public static void FillFakeData(){
        Aktivnost aktivnost = new Aktivnost();
        aktivnost.setDistance(10000);
        aktivnost.setName("fake 1");
        aktivnost.save();

        aktivnost = new Aktivnost();
        aktivnost.setDistance(12333);
        aktivnost.setName("fake 2");
        aktivnost.save();

    }

   /* public static void FillActivities(){
        String[]
    }*/
}
