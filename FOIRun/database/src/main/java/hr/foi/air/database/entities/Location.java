package hr.foi.air.database.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import hr.foi.air.database.FoiDatabase;

@Table(database = FoiDatabase.class)
public class Location extends BaseModel {

    @PrimaryKey(autoincrement = true)
    int id;

    @Column int lap;
    @Column int type;
    @Column long time; // in milliseconds since epoch
    @Column double latitude;
    @Column double longitude;
    @Column double accurancy;
    @Column double altitude;
    @Column double speed;
    @Column double bearing;
    @Column int hr;
    @Column double cadence;

    @Column int activity_id;

    @Column
    @ForeignKey(tableClass = Aktivnost.class)
    Aktivnost aktivnost;

    public Location(){

    }

    public Location(int lap, int type, long time, double latitude, double longitude, double accurancy, double altitude, double speed, double bearing, int hr, double cadence) {
        this.lap = lap;
        this.type = type;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accurancy = accurancy;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.hr = hr;
        this.cadence = cadence;
    }

    public static List<Location> getAll(){
        return SQLite.select().from(Location.class).queryList();
    }
}
