package hr.foi.air.foirun.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.trophies.events.NumberOfActivitiesEvent;
import com.example.trophies.events.RecordDistanceEvent;
import com.example.trophies.events.SaveDistanceEvent;
import com.example.trophies.events.TotalDistanceEvent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Random;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.database.entities.Location;
import hr.foi.air.foirun.events.BusProvider;


public class ActivityTracker extends LocationTracker {

    private Aktivnost mActivity;
    private Context mContext;

    private int lap;
    private GoogleMap mMap;
    private boolean isFirstLocation = true;

    public ActivityTracker(Context context) {

        super(context, ActivityTracker.GetDefaultSettings());

        //OVO mora biti ma da ne vidim point pa ostavljam prazno
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }

        mContext = context;

    }

    @Override
    public void onLocationFound(@NonNull android.location.Location location) {

        //If distance is zero just continue don't use resources
        if((mActivity.getId() > 0 && getLastDistance(location) > 0) || isFirstLocation){

            isFirstLocation = false;

            //if first last distance in range of 4m it is a lap
            double dist = getFirstLastDistance(location);
            if(dist < 4.0 && dist > 0) lap++;

            Location eLocation = new Location();

            if(location.hasAccuracy()) eLocation.setAccurancy(location.getAccuracy());

            if(location.hasAltitude()) eLocation.setAltitude(location.getAltitude());

            if(location.hasBearing()) eLocation.setBearing(location.getBearing());

            if(location.hasSpeed()) eLocation.setSpeed(location.getSpeed());

            //TODO: read heart rate from wear device
            Random rand = new Random(80);
            eLocation.setHr(rand.nextInt(150));

            //TODO: calculate rhytmn
            eLocation.setCadence(1);

            eLocation.setLatitude(location.getLatitude());
            eLocation.setLongitude(location.getLongitude());

            setLocation(new LatLng(eLocation.getLatitude(), eLocation.getLongitude()));

            eLocation.setTime(location.getTime());
            eLocation.setAktivnost(this.mActivity);

            eLocation.setLap(lap);

            eLocation.setActivity_id(this.mActivity.getId());

            mActivity.getLocationList().add(eLocation);

            mActivity.save();
        }
    }

    @Override
    public void onTimeout() {
        Toast.makeText(mContext, "Location tracker timeouts", Toast.LENGTH_LONG).show();
    }


    public void Start(String name, String comment, int typeId){

        //OVO mora biti ma da ne vidim point pa ostavljam prazno
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }

        mActivity = new Aktivnost();

        isFirstLocation = true;

        lap = 1;

        mActivity.setName(name);

        mActivity.setStart_time(getCurrentMilis());
        mActivity.setType_id(typeId);
        mActivity.setComment(comment);

        if(mActivity.insert() > 0){
            this.startListening();
        } else {
            throw new IllegalStateException("Activity not saved");
        }
    }

    public void Stop(){


        try {
            this.stopListening();
        } catch (SecurityException sex){
            throw sex;
        }

        BusProvider.getInstance().unregister(this);

        mActivity.setTime(getDuration());
        mActivity.setDistance(getDistance());
        mActivity.setAvg_cadence(getAvgCadence());
        mActivity.setAvg_hr(getAvgHr());
        mActivity.setAvg_hr(getMaxHr());

        mActivity.save();

        long km = 0;
        for (Aktivnost a : Aktivnost.getAll()) {
            km += a.getDistance();
        }

        EventBus.getDefault().post(new SaveDistanceEvent(km));
        EventBus.getDefault().post(new NumberOfActivitiesEvent());
        EventBus.getDefault().post(new RecordDistanceEvent());
        EventBus.getDefault().post(new TotalDistanceEvent());

    }

    public long getCurrentMilis() {

        DateTime dt = DateTime.now();
        return dt.getMillis();

    }

    public double getAvgCadence() {

        List<Location> locations = mActivity.getLocationList();

        double sum = 0;
        int count = locations.size();

        for (Location location : locations) {

            sum += location.getCadence();

        }

        return sum / count;
    }

    public double getAvgHr() {
        List<Location> locations = mActivity.getLocationList();

        double sum = 0;
        int count = locations.size();

        for (Location location : locations) {

            sum += location.getHr();

        }

        return sum / count;
    }

    public int getMaxHr() {
        List<Location> locations = mActivity.getLocationList();

        int max = 0;

        for (Location location : locations) {

            if(location.getHr() > max){
                max = location.getHr();
            }

        }

        return max;
    }


    //Setting path we are running dynamically because if somebody needs to check route when running
    private boolean setLocation(LatLng current){

        List<Location> locations = mActivity.getLocationList();

        int locCount = locations.size();

        if(locCount == 0){
            //adding start
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.addMarker(new MarkerOptions().position(current));
        }

        if(mMap == null || locCount < 1) return false;

        Location prevLocation = locations.get(locCount - 1);
        LatLng prev = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(current, 16);
        mMap.animateCamera(update);
        mMap.addPolyline((new PolylineOptions())
                .add(prev, current).width(6).color(Color.BLUE)
                .visible(true));

        return true;
    }

    public double getLastDistance(android.location.Location curr) {
        List<Location> locations = mActivity.getLocationList();

        int locCount = locations.size();

        if(locCount < 1) return -1;

        Location prev = locations.get(locCount - 1);

        float[] results = new float[3];
        android.location.Location.distanceBetween(prev.getLatitude(), prev.getLongitude(),
                curr.getLatitude(), curr.getLongitude(), results);

        //We are only using distance for now
        //On positions 1 and 2 are bearings (start and stop)
        return results[0];
    }

    public double getFirstLastDistance(android.location.Location last) {
        List<Location> locations = mActivity.getLocationList();

        int locCount = locations.size();

        if(locCount < 1) return -1;

        Location first = locations.get(0);

        float[] results = new float[3];
        android.location.Location.distanceBetween(first.getLatitude(), first.getLongitude(),
                last.getLatitude(), last.getLongitude(), results);

        //We are only using distance for now
        //On positions 1 and 2 are bearings (start and stop)
        return results[0];
    }

    public double getDistance() {
        List<Location> locations = mActivity.getLocationList();

        double dist = 0;

        for (int i = 0; i < locations.size() - 1; i++){
            Location first = locations.get(i);
            Location second = locations.get(i + 1);

            float[] results = new float[3];
            android.location.Location.distanceBetween(first.getLatitude(), first.getLongitude(),
                    second.getLatitude(), second.getLongitude(), results);

            //We are only using distance for now
            //On positions 1 and 2 are bearings (start and stop)
            dist += results[0];
        }

        return dist;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    private static TrackerSettings GetDefaultSettings() {
        return new TrackerSettings()
                .setUseGPS(true)
                .setUseNetwork(true)
                .setUsePassive(true)
                .setTimeBetweenUpdates(2 * 1000)
                .setMetersBetweenUpdates(1);
    }

    public long getDuration() {
        List<Location> locations = mActivity.getLocationList();

        int locCount = locations.size();

        if(locCount < 2) return  0;

        return locations.get(locCount - 1).getTime() - locations.get(0).getTime();
    }

    public Aktivnost getAktivnost() {
        return mActivity;
    }

    public void clearMap() {
//        mMap.clear();
    }
}
