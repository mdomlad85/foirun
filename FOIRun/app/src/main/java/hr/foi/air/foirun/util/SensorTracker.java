package hr.foi.air.foirun.util;


import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import hr.foi.air.foirun.R;
import hr.foi.air.foirun.data.DataEntry;
import hr.foi.air.foirun.data.Sensor;
import hr.foi.air.foirun.data.SensorDataPoint;
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.SensorRangeEvent;
import hr.foi.air.foirun.events.SensorUpdatedEvent;
import io.realm.Realm;

public class SensorTracker {

    private static SensorTracker mInstance;

    private Sensor sensor;
    private float spread;

    private Realm mRealm;
    private String mAndroidId;

    private Activity mActivity;

    private TextView mHrTxt;

    public SensorTracker(long sensorId, Activity activity) {
        mActivity = activity;
        sensor = RemoteSensorManager.getInstance(mActivity).getSensor(sensorId);
        mHrTxt = (TextView) mActivity.findViewById(R.id.hr_value_text);
    }

    private void initialiseSensorData() {

        if(sensor == null) return;

        spread = sensor.getMaxValue() - sensor.getMinValue();
        LinkedList<SensorDataPoint> dataPoints = sensor.getDataPoints();

        if (dataPoints == null || dataPoints.isEmpty()) {
            Log.w("sensor data", "no data found for sensor " + sensor.getId() + " " + sensor.getName());
            return;
        }


        ArrayList<Float>[] normalisedValues = new ArrayList[dataPoints.getFirst().getValues().length];
        ArrayList<Integer>[] accuracyValues = new ArrayList[dataPoints.getFirst().getValues().length];
        ArrayList<Long>[] timestampValues = new ArrayList[dataPoints.getFirst().getValues().length];


        for (int i = 0; i < normalisedValues.length; ++i) {
            normalisedValues[i] = new ArrayList<>();
            accuracyValues[i] = new ArrayList<>();
            timestampValues[i] = new ArrayList<>();
        }


        for (SensorDataPoint dataPoint : dataPoints) {

            for (int i = 0; i < dataPoint.getValues().length; ++i) {
                float normalised = (dataPoint.getValues()[i] - sensor.getMinValue()) / spread;
                normalisedValues[i].add(normalised);
                accuracyValues[i].add(dataPoint.getAccuracy());
                timestampValues[i].add(dataPoint.getTimestamp());
            }
        }

    }

    public void Resume() {
        initialiseSensorData();

        mRealm = Realm.getInstance(mActivity);
        mAndroidId = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);

        Attach();
    }

    public void Attach() {
        BusProvider.getInstance().register(this);

        Random rnd = new Random(80);
        String txt = String.valueOf(rnd.nextInt(150));
        mHrTxt.setText(txt);
    }

    public void Detach() {
        BusProvider.getInstance().unregister(this);
        mHrTxt.setText("?");
    }

    @Subscribe
    public void onSensorUpdatedEvent(SensorUpdatedEvent event) {
        if (event.getSensor().getId() == this.sensor.getId()) {

            mRealm.beginTransaction();
            DataEntry entry = mRealm.createObject(DataEntry.class);
            entry.setAndroidDevice(mAndroidId);
            entry.setTimestamp(event.getDataPoint().getTimestamp());
            if (event.getDataPoint().getValues().length > 0) {
                entry.setX(event.getDataPoint().getValues()[0]);
            } else {
                entry.setX(0.0f);
            }

            if (event.getDataPoint().getValues().length > 1) {
                entry.setY(event.getDataPoint().getValues()[1]);
            } else {
                entry.setY(0.0f);
            }

            if (event.getDataPoint().getValues().length > 2) {
                entry.setZ(event.getDataPoint().getValues()[2]);
            } else {
                entry.setZ(0.0f);
            }

            entry.setAccuracy(event.getDataPoint().getAccuracy());
            entry.setDatasource("Acc");
            entry.setDatatype(event.getSensor().getId());
            mRealm.commitTransaction();

            for (int i = 0; i < event.getDataPoint().getValues().length; ++i) {
                float normalised = (event.getDataPoint().getValues()[i] - sensor.getMinValue()) / spread;
            }
        }
    }

    @Subscribe
    public void onSensorRangeEvent(SensorRangeEvent event) {
        if (event.getSensor().getId() == this.sensor.getId()) {
            initialiseSensorData();
        }
    }

    public static SensorTracker newInstance(int type, Activity activity) {

        if(mInstance == null){
            mInstance = new SensorTracker(type, activity);
        }

        return mInstance;
    }
}
