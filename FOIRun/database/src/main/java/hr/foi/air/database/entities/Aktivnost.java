package hr.foi.air.database.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import hr.foi.air.database.FoiDatabase;

@Table(database = FoiDatabase.class)
public class Aktivnost extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column int id;

    @Column long start_time;
    @Column double distance;
    @Column long time;
    @Column String name;
    @Column String comment;
    @Column double avg_hr;
    @Column int max_hr;
    @Column double avg_cadence;
    @Column boolean deleted;
    @Column int type_id;
    @Column int user_id;
    @Column(defaultValue = "0") boolean is_exercise;


    public Aktivnost(long start_time, double distance, long time, String name, String comment,
                     int type_id, int avg_hr, int max_hr, double avg_cadence, boolean deleted) {

        this.start_time = start_time;
        this.distance = distance;
        this.time = time;
        this.name = name;
        this.comment = comment;
        this.type_id = type_id;
        this.avg_hr = avg_hr;
        this.max_hr = max_hr;
        this.avg_cadence = avg_cadence;
        this.deleted = deleted;

    }

    public Aktivnost() {

    }

    public static List<Aktivnost> getAll(){
        return SQLite.select().from(Aktivnost.class).queryList();
    }

    public static List<Aktivnost> getExercises() {
        return new Select().from(Aktivnost.class)
                .where(Aktivnost_Table.is_exercise.eq(true))
                .queryList();
    }

    List<Location> locationList;

    public List<Location> getLocationList(){
        if(locationList == null || locationList.isEmpty()){
            locationList = new Select().from(Location.class)
                    .where(Location_Table.activity_id.eq(id))
                    .queryList();
        }
        return locationList;
    }

    public static List<Aktivnost> getByUserId(int uid) {

        return new Select().from(Aktivnost.class)
                .where(Aktivnost_Table.user_id.eq(uid))
                .queryList();

    }

    public int getId() {
        return id;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getAvg_hr() {
        return avg_hr;
    }

    public void setAvg_hr(double avg_hr) {
        this.avg_hr = avg_hr;
    }

    public int getMax_hr() {
        return max_hr;
    }

    public void setMax_hr(int max_hr) {
        this.max_hr = max_hr;
    }

    public double getAvg_cadence() {
        return avg_cadence;
    }

    public void setAvg_cadence(double avg_cadence) {
        this.avg_cadence = avg_cadence;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public boolean is_exercise() {
        return is_exercise;
    }

    public void setIs_exercise(boolean is_exercise) {
        this.is_exercise = is_exercise;
    }
}
