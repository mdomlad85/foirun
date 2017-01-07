package hr.foi.air.foirun;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.air.database.FoiDatabase;
import hr.foi.air.foirun.data.Sensor;
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.NewSensorEvent;
import hr.foi.air.foirun.ui.StartActivityFragment;
import hr.foi.air.foirun.ui.StopActivityFragment;
import hr.foi.air.foirun.util.ActivityTracker;
import hr.foi.air.foirun.util.RemoteSensorManager;
import hr.foi.air.foirun.util.SensorTracker;

import static android.R.attr.fragment;
import static android.R.attr.mapViewStyle;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityTracker mTracker;
    private SensorTracker mSTracker;
    private RemoteSensorManager remoteSensorManager;

    @BindView(R.id.start_button)
    Button startBtn;

    @BindView(R.id.wear_button)
    ImageButton wearBtn;

    @BindView(R.id.start_buttons)
    LinearLayout startBtns;

    private SupportMapFragment mapFragment;
    private StartActivityFragment startFragment;
    private StopActivityFragment stopFragment;

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

        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);

        remoteSensorManager = RemoteSensorManager.getInstance(this);
        remoteSensorManager.addTag("HEART_RATE");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @OnClick(R.id.start_button)
    public void onStartActivity(View view){

        if(view.getId() == R.id.start_button){

            String start = getResources().getString(R.string.Start_Activity);
            String stop = getResources().getString(R.string.Stop_Activity);

            if(startBtn.getText().toString().equals(start)){
                this.Start(stop);
            } else {
                this.Stop(start);
            }
        }
    }

    @OnClick(R.id.save_activity)
    public void onSaveActivity(View view){

        if(view.getId() == R.id.save_activity){

            PrepearePodium();

            Intent intent = getIntent();
            int uid = intent.getIntExtra("uid",  0);

            mTracker.getAktivnost().setUser_id(uid);
            mTracker.getAktivnost().save();

            Toast.makeText(this,
                    String.format("Activity %s saved", mTracker.getAktivnost().getName()),
                    Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.delete_activity)
    public void onDeleteActivity(View view){

        if(view.getId() == R.id.delete_activity){

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

        if(!startFragment.isValid()){
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
