package hr.foi.air.foirun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
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
import hr.foi.air.foirun.data.SensorNames;
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.NewSensorEvent;
import hr.foi.air.foirun.util.ActivityTracker;
import hr.foi.air.foirun.util.RemoteSensorManager;
import hr.foi.air.foirun.util.SensorTracker;

public class MainActivity extends AppCompatActivity {

    private ActivityTracker mTracker;
    private SensorTracker mSTracker;
    private RemoteSensorManager remoteSensorManager;

    @BindView(R.id.start_button)
    Button startBtn;

    @BindView(R.id.wear_button)
    ImageButton wearBtn;
    private List<Node> mNodes;
    private List<Sensor> mSensors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        ButterKnife.bind(this);
        JodaTimeAndroid.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        //FoiDatabase.FillActivityTracker();

        mTracker = new ActivityTracker(this);
        remoteSensorManager = RemoteSensorManager.getInstance(this);
        remoteSensorManager.addTag("HEART_RATE");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @OnClick(R.id.start_button)
    public void onStartActivity(View view){

        if(view.getId() == R.id.start_button){

            mTracker.Start("Test", 1, false);

            List<hr.foi.air.foirun.data.Sensor> sensors = RemoteSensorManager.getInstance(this).getSensors();

            mSTracker = SensorTracker.newInstance(Sensor.TYPE_HEART_RATE, this);
            mSTracker.Attach();

            remoteSensorManager.startMeasurement();

            startBtn.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.wear_button)
    public void onStopActivity(View view){

        if(view.getId() == R.id.wear_button){

            mTracker.Stop();
            remoteSensorManager.stopMeasurement();
            startBtn.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);

        remoteSensorManager.stopMeasurement();
    }
    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        mSensors = RemoteSensorManager.getInstance(this).getSensors();

        remoteSensorManager.startMeasurement();


    }




    private void notifyUSerForNewSensor(Sensor sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        notifyUSerForNewSensor(event.getSensor());
    }

}
