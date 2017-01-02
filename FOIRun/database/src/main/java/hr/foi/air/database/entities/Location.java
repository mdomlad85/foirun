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

    public Location(int lap, long time, double latitude, double longitude, double accurancy, double altitude, double speed, double bearing, int hr, double cadence) {
        this.lap = lap;
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

    public int getId() {
        return id;
    }

    public int getLap() {
        return lap;
    }

    public void setLap(int lap) {
        this.lap = lap;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccurancy() {
        return accurancy;
    }

    public void setAccurancy(double accurancy) {
        this.accurancy = accurancy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public double getCadence() {
        return cadence;
    }

    public void setCadence(double cadence) {
        this.cadence = cadence;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public Aktivnost getAktivnost() {
        return aktivnost;
    }

    public void setAktivnost(Aktivnost aktivnost) {
        this.aktivnost = aktivnost;
    }
}
