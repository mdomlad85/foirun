package hr.foi.air.foirun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eminayar.panter.PanterDialog;
import com.example.trophies.Trophy;
import com.example.trophies.Trophy1;
import com.example.trophies.Trophy2;
import com.example.trophies.Trophy3;
import com.example.trophies.Trophy4;
import com.example.trophies.events.NumberOfActivitiesEvent;
import com.example.trophies.events.RecordDistanceEvent;
import com.example.trophies.events.SaveDistanceEvent;
import com.example.trophies.events.TotalDistanceEvent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;


import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.air.database.FoiDatabase;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.foirun.adapter.AktivnostListAdapter;
import hr.foi.air.foirun.data.Sensor;
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.NewSensorEvent;
import hr.foi.air.foirun.fragments.StartActivityFragment;
import hr.foi.air.foirun.fragments.StopActivityFragment;
import hr.foi.air.foirun.util.ActivityTracker;
import hr.foi.air.foirun.util.RemoteSensorManager;
import hr.foi.air.foirun.util.SensorTracker;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityTracker mTracker;
    private SensorTracker mSTracker;
    private RemoteSensorManager remoteSensorManager;

    @BindView(R.id.show_myactivies)
    Button showBtn;

    @BindView(R.id.start_button)
    Button startBtn;

    @BindView(R.id.wear_button)
    ImageButton wearBtn;

    @BindView(R.id.start_buttons)
    LinearLayout startBtns;

    @BindView(R.id.scoreboard)
    ListView scoreboard;

    private SupportMapFragment mapFragment;
    private StartActivityFragment startFragment;
    private StopActivityFragment stopFragment;
    private boolean isInListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        mTracker = new ActivityTracker(this);

        startFragment = (StartActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.startactivity_fragment);

        stopFragment = (StopActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.stopactivity_fragment);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        JodaTimeAndroid.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        FoiDatabase.FillActivityTracker();
        //FoiDatabase.FillFakeData();

        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);
        scoreboard.setVisibility(View.INVISIBLE);

        remoteSensorManager = RemoteSensorManager.getInstance(this);
        remoteSensorManager.addTag("HEART_RATE");


        double km = 0;
        for (Aktivnost a : Aktivnost.getAll()) {
            km += a.getDistance();
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @OnClick(R.id.show_myactivies)
    public void onShowActivities(View view) {

        if (view.getId() == R.id.show_myactivies) {

            int uid = getIntent().getIntExtra("uid", 0);
            List<Aktivnost> aktivnosti = Aktivnost.getByUserId(uid);

            AktivnostListAdapter adapter = new AktivnostListAdapter(this, aktivnosti);

            scoreboard.setAdapter(adapter);
            scoreboard.setVisibility(View.VISIBLE);
            startFragment.getView().setVisibility(View.INVISIBLE);
            startBtns.setVisibility(View.INVISIBLE);

            isInListView = true;
        }
    }

    //Push back button to go back from list view
    @Override
    public void onBackPressed() {

        if (isInListView) {

            scoreboard.setVisibility(View.INVISIBLE);
            startFragment.getView().setVisibility(View.VISIBLE);
            startBtns.setVisibility(View.VISIBLE);

            isInListView = false;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveDistanceEvent event) {/* Do something */
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSettings.edit();
        double km = 0;
        for (Aktivnost a : Aktivnost.getAll()) {
            km += a.getDistance();
        }

        long lastshown = mSettings.getLong("last_shown", 0);
        long to10km = mSettings.getLong("to10km", 0);

        if (km >= 10000) {
            double remainder = km % 10000;

            if (km >= (lastshown + 10000)) {
                //showdialog, save data
                editor.putLong("last_shown", ((long) km));
                //editor.putLong("to10km", ((long) remainder));
                editor.apply();
                show1stTrophyMessage();
            }


        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecordDistanceEvent event) /* Do something */ {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSettings.edit();
        long maxDistance = 0;
        long maxSaved = mSettings.getLong("max_distance", 0);

        for (Aktivnost a : Aktivnost.getAll()) {
            if (a.getDistance() >= maxDistance) {
                maxDistance = (long) a.getDistance();
            }
        }
        editor.putLong("max_distance", maxDistance);
        editor.apply();

        if (maxDistance > maxSaved) {
            show3rdTrophyMessage();
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TotalDistanceEvent event) {
        long totalDistance = 0;

        for (Aktivnost a : Aktivnost.getAll()) {
            totalDistance += a.getDistance();
        }


        if (totalDistance >=50 && totalDistance <100){
            //the rookie
            show4thTrophyMessage("The rookie");
        } else if (totalDistance >= 100 && totalDistance < 200) {
            //the jogger
            show4thTrophyMessage("The jogger");

        }else if (totalDistance >= 200 && totalDistance < 300) {
            //the exhausted
            show4thTrophyMessage("The exhausted");

        }else if (totalDistance >= 300 && totalDistance < 500) {
            //the road marshal
            show4thTrophyMessage("The road marshal");

        }else if (totalDistance >= 500 && totalDistance < 750) {
            //the inexhaustible
            show4thTrophyMessage("The inexhaustible");

        }else if (totalDistance >= 750 && totalDistance < 1000) {
            //lord of the road
            show4thTrophyMessage("Lord of the road");

        }else if (totalDistance >= 1000) {
            //the unstoppable
            show4thTrophyMessage("The unstoppable");

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NumberOfActivitiesEvent event) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSettings.edit();


        if (Aktivnost.getAll().size() - mSettings.getLong("activities_number", 0) >= 5) {
            show2stTrophyMessage();
            editor.putLong("activities_number", Aktivnost.getAll().size());
            editor.apply();
        }
    }

    public void show1stTrophyMessage() {
        Trophy trophy1 = new Trophy1();
        trophy1.showDialog(this, "");
    }

    public void show2stTrophyMessage() {
        Trophy trophy2 = new Trophy2();
        trophy2.showDialog(this, "");
    }

    public void show3rdTrophyMessage() {
        Trophy trophy3 = new Trophy3();
        trophy3.showDialog(this, "");
    }

    public void show4thTrophyMessage(String text) {
        Trophy trophy4 = new Trophy4();
        trophy4.showDialog(this, text);
    }

    @Override
    protected void onStart() {

        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @OnClick(R.id.start_button)
    public void onStartActivity(View view) {

        if (view.getId() == R.id.start_button) {

            String start = getResources().getString(R.string.Start_Activity);
            String stop = getResources().getString(R.string.Stop_Activity);

            if (startBtn.getText().toString().equals(start)) {
                this.Start(stop);
            } else {
                this.Stop(start);
            }
        }
    }

    @OnClick(R.id.save_activity)
    public void onSaveActivity(View view) {

        if (view.getId() == R.id.save_activity) {

            PrepearePodium();

            int uid = getIntent().getIntExtra("uid", 0);

            mTracker.getAktivnost().setUser_id(uid);
            mTracker.getAktivnost().save();

            Toast.makeText(this,
                    String.format("Activity %s saved", mTracker.getAktivnost().getName()),
                    Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.delete_activity)
    public void onDeleteActivity(View view) {

        if (view.getId() == R.id.delete_activity) {

            PrepearePodium();

            mTracker.getAktivnost().delete();

            Toast.makeText(this,
                    String.format("Activity %s deleted", mTracker.getAktivnost().getName()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void PrepearePodium() {

        mTracker.clearMap();
        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);
        startFragment.getView().setVisibility(View.VISIBLE);
        startFragment.ClearForm();
        startBtns.setVisibility(View.VISIBLE);

    }

    private void Stop(String start) {

        startBtns.setVisibility(View.INVISIBLE);
        startBtn.setText(start);
        mTracker.Stop();
        mSTracker.Detach();
        remoteSensorManager.stopMeasurement();
        BusProvider.getInstance().unregister(this);

        stopFragment.setActivityTxt(mTracker.getAktivnost().getType_id());
        stopFragment.setDistanceTxt(mTracker.getAktivnost().getDistance());
        stopFragment.setTimeTxt(mTracker.getAktivnost().getTime());

        stopFragment.getView().setVisibility(View.VISIBLE);

    }

    private void Start(String stop) {

        if (!startFragment.isValid()) {
            Toast.makeText(this, "You must enter title.", Toast.LENGTH_LONG).show();
            return;
        }

        startBtn.setText(stop);

        mTracker.Start(startFragment.getName(),
                startFragment.getComment(), startFragment.getTypeId());

        BusProvider.getInstance().register(this);

        mSTracker = SensorTracker.newInstance(Sensor.TYPE_HEART_RATE, this);

        mSTracker.Attach();

        remoteSensorManager.startMeasurement();

        mapFragment.getView().setVisibility(View.VISIBLE);
        startFragment.getView().setVisibility(View.INVISIBLE);
    }

    private void notifyUSerForNewSensor(Sensor sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        notifyUSerForNewSensor(event.getSensor());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMinZoomPreference(16);
        googleMap.setMaxZoomPreference(18);

        //Map is used to show and track movement
        //if somebody wanna look where is moving
        mTracker.setmMap(googleMap);
    }


}
