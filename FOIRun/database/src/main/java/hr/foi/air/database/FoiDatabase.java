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
    public static final int VERSION = 7;

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
        aktivnost.setDistance(250);
        aktivnost.setName("fake 1");
        aktivnost.save();

        aktivnost = new Aktivnost();
        aktivnost.setDistance(250);
        aktivnost.setName("fake 2");
        aktivnost.save();



    }

    public static void FillExerciseData(){
        List<Aktivnost> exercises = Aktivnost.getExercises();

        if(exercises.size() == 0) {
            List<ActivityType> types = ActivityType.getAll();
            int[] distances = {1000, 2500, 5000, 10000, 22500, 43000, 100000};

            for (int distance : distances) {
                makeActivity(distance, types);
            }
        }
    }

    private static void makeActivity(int distance, List<ActivityType> types) {

        for(ActivityType type: types) {
            Aktivnost aktivnost = new Aktivnost();
            aktivnost.setDistance(distance);
            float kms = distance / 1000;
            aktivnost.setName(String.format("%.2f km %s", kms, type.getName()));
            aktivnost.setComment(String.format("%s for %.2f kilometer", type.getName(), kms));
            aktivnost.setType_id(type.getId());
            aktivnost.setIs_exercise(true);
            aktivnost.save();
        }
    }
}
