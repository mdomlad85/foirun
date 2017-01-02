package hr.foi.air.foirun.util;

import android.content.Context;
import android.location.Address;

import com.entire.sammalik.samlocationandgeocoding.SamLocationRequestService;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Random;

import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.database.entities.Location;


public class ActivityTracker extends SamLocationRequestService implements SamLocationRequestService.SamLocationListener {

    private Aktivnost mActivity;

    private int lap;

    public ActivityTracker(Context context) {
        super(context);
        mActivity = new Aktivnost();
        this.executeService(this);
    }



    public void Start(String name, int typeId, boolean isWithComment){

        mActivity.setName(name);

        mActivity.setStart_time(getCurrentMilis());
        mActivity.setType_id(typeId);

        if(!isWithComment){
            if(mActivity.insert() > 0){
                super.startLocationUpdates();
            } else {
                throw new IllegalStateException("Activity not saved");
            }
        }
    }

    public void Start(String name, String comment, int typeId){

        this.Start(name, typeId, true);
        mActivity.setComment(comment);

        if(mActivity.insert() > 0){
            super.startLocationUpdates();
        } else {
            throw new IllegalStateException("Activity not saved");
        }
    }

    public void Stop(){

        this.stopLocationUpdates();

        long time = getCurrentMilis() - mActivity.getStart_time();
        mActivity.setTime(time);
        mActivity.setDistance(getDistance());
        mActivity.setAvg_cadence(getAvgCadence());
        mActivity.setAvg_hr(getAvgHr());
        mActivity.setAvg_hr(getMaxHr());
        mActivity.save();
    }

    @Override
    public void onLocationUpdate(android.location.Location location, Address address) {

        if(mActivity.getId() > 0){

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
            eLocation.setTime(getCurrentMilis());
            eLocation.setAktivnost(this.mActivity);

            //TODO: calculate lap
            eLocation.setLap(lap);

            eLocation.setActivity_id(this.mActivity.getId());

            eLocation.save();
        }
    }

    public long getCurrentMilis() {

        DateTime dt = DateTime.now();
        return dt.getMillis();

    }

    public double getDistance() {
        List<Location> locations = mActivity.getLocationList();

        Location first = locations.get(0);
        Location last = locations.get(locations.size() - 1);


        float[] results = new float[3];
        android.location.Location.distanceBetween(first.getLatitude(), first.getLongitude(),
                last.getLatitude(), last.getLongitude(), results);

        //We are only using distance for now
        //On positions 1 and 2 are bearings (start and stop)
        return results[0];
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
}
