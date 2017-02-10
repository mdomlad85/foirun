package hr.foi.air.foirun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Set;
import com.raizlabs.android.dbflow.sql.language.Update;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.air.database.FoiDatabase;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.database.entities.User;
import hr.foi.air.database.entities.User_Table;
import hr.foi.air.foirun.adapter.AktivnostListAdapter;
import hr.foi.air.foirun.data.Sensor;
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.NewSensorEvent;
import hr.foi.air.foirun.fragments.StartActivityFragment;
import hr.foi.air.foirun.fragments.StopActivityFragment;
import hr.foi.air.foirun.fragments.UserProfileFragment;
import hr.foi.air.foirun.util.ActivityTracker;
import hr.foi.air.foirun.util.RemoteSensorManager;
import hr.foi.air.foirun.util.SensorTracker;

import static hr.foi.air.database.entities.User_Table.name;
import static hr.foi.air.database.entities.User_Table.realname;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityTracker mTracker;
    private SensorTracker mSTracker;
    private RemoteSensorManager remoteSensorManager;

    @BindView(R.id.show_myactivies)
    Button showBtn;

    @BindView(R.id.start_button)
    Button startBtn;

    @BindView(R.id.userProfile_button)
    Button userProfileBtn;

    @BindView(R.id.wear_button)
    ImageButton wearBtn;

    @BindView(R.id.start_buttons)
    LinearLayout startBtns;

    @BindView(R.id.scoreboard)
    ListView scoreboard;

    @BindView(R.id.updateuser_button)
    Button updateUserBtn;

    @BindView(R.id.saveuserchanges_button)
    Button saveUserChangesBtn;

    private SupportMapFragment mapFragment;
    private StartActivityFragment startFragment;
    private StopActivityFragment stopFragment;
    private UserProfileFragment userProfileFragment;
    private boolean isInListView;

    private EditText username;
    private EditText realname;
    private EditText lastname;
    private EditText email;
    private EditText age;
    private EditText height;
    private EditText weight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        mTracker = new ActivityTracker(this);

        userProfileFragment = (UserProfileFragment) getSupportFragmentManager()
                .findFragmentById(R.id.userProfile_fragment);

        startFragment = (StartActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.startactivity_fragment);

        stopFragment = (StopActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.stopactivity_fragment);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        username = (EditText) findViewById(R.id.dbusername);
        realname = (EditText) findViewById(R.id.dbrealname);
        lastname = (EditText) findViewById(R.id.dblastname);
        email = (EditText) findViewById(R.id.dbemail);
        age = (EditText) findViewById(R.id.dbage);
        height = (EditText) findViewById(R.id.dbheight);
        weight = (EditText) findViewById(R.id.dbweight);

        ButterKnife.bind(this);
        JodaTimeAndroid.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        FoiDatabase.FillActivityTracker();

        userProfileFragment.getView().setVisibility(View.INVISIBLE);
        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);
        scoreboard.setVisibility(View.INVISIBLE);

        remoteSensorManager = RemoteSensorManager.getInstance(this);
        remoteSensorManager.addTag("HEART_RATE");


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }


    @OnClick(R.id.show_myactivies)
    public void onShowActivities(View view){

        if(view.getId() == R.id.show_myactivies){

            int uid = getIntent().getIntExtra("uid",  0);
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
        if(userProfileFragment.isVisible()){
            if(updateUserBtn.getText() == "Cancel"){ openDialog();}
            else {
                userProfileFragment.getView().setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
            }
        }
        else {
            if (isInListView) {

                scoreboard.setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
                startBtns.setVisibility(View.VISIBLE);

                isInListView = false;

            }
        }
    }

    @OnClick(R.id.userProfile_button)
    public void onUserProfileActivity(View view){
        if(view.getId() == R.id.userProfile_button){
            startFragment.getView().setVisibility(View.INVISIBLE);
            userProfileFragment.getView().setVisibility(View.VISIBLE);
            updateUserBtn.setVisibility(View.VISIBLE);
        }
        int uid = getIntent().getIntExtra("uid",  0);

        username.setText(User.getById(uid).getName());
        realname.setText(User.getById(uid).getRealname());
        lastname.setText(User.getById(uid).getLastname());
        email.setText(User.getById(uid).getEmail());
        if(User.getById(uid).getAge() != 0) { age.setText(String.valueOf(User.getById(uid).getAge())); }
        if(User.getById(uid).getHeight() != 0) { height.setText(String.valueOf(User.getById(uid).getHeight())); }
        if(User.getById(uid).getWeight() != 0) { weight.setText(String.valueOf(User.getById(uid).getWeight())); }
    }

    @OnClick(R.id.updateuser_button)
    public void onUpdateUser(){

        if(updateUserBtn.getText() == "Update"){
            updateClicked();
        }
        else{
            cancelClicked();
        }

    }

    public void updateClicked(){
        username.setEnabled(true);
        realname.setEnabled(true);
        lastname.setEnabled(true);
        email.setEnabled(true);
        age.setEnabled(true);
        height.setEnabled(true);
        weight.setEnabled(true);
        updateUserBtn.setText("Cancel");

        saveUserChangesBtn.setVisibility(View.VISIBLE);
    }

    public void cancelClicked(){
        username.setEnabled(false);
        realname.setEnabled(false);
        lastname.setEnabled(false);
        email.setEnabled(false);
        age.setEnabled(false);
        height.setEnabled(false);
        weight.setEnabled(false);
        updateUserBtn.setText("Update");

        saveUserChangesBtn.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.saveuserchanges_button)
    public void onSaveUserChanges(){
        int uid = getIntent().getIntExtra("uid",  0);
        String uname = username.getText().toString();
        String urname = realname.getText().toString();
        String ulname = lastname.getText().toString();
        String uemail = email.getText().toString();
        int uage = Integer.valueOf(String.valueOf(age.getText()));
        int uheight = Integer.valueOf(String.valueOf(height.getText()));
        int uweight = Integer.valueOf(String.valueOf(weight.getText()));

        User.updateUser(uid, uname, urname, ulname, uemail, uage, uheight, uweight);

        String msg = "User has been updated!";

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        cancelClicked();
    }

    @OnClick(R.id.start_button)
    public void onStartActivity(View view){

        if(userProfileFragment.isVisible()){
            if(updateUserBtn.getText() == "Cancel"){ openDialog();}
            else{
                userProfileFragment.getView().setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
            }

        }
        else {
            if (view.getId() == R.id.start_button) {

                String start = getResources().getString(R.string.Start_Activity);
                String stop = getResources().getString(R.string.Stop_Activity);

                if (userProfileFragment.isVisible()) {
                    userProfileFragment.getView().setVisibility(View.INVISIBLE);
                    startFragment.getView().setVisibility(View.VISIBLE);
                }

                if (startBtn.getText().toString().equals(start)) {
                    this.Start(stop);
                } else {
                    this.Stop(start);
                }
            }
        }
    }


    @OnClick(R.id.save_activity)
    public void onSaveActivity(View view){

        if(view.getId() == R.id.save_activity){

            PrepearePodium();

            int uid = getIntent().getIntExtra("uid",  0);

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

    public void openDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Cancel update");
        dialog.setMessage("Are you sure you want to cancel user update?" );
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancelClicked();
                userProfileFragment.getView().setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void PrepearePodium() {

        mTracker.clearMap();
        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);
        startFragment.getView().setVisibility(View.VISIBLE);
        startFragment.ClearForm();
        userProfileFragment.getView().setVisibility(View.INVISIBLE);
        startBtns.setVisibility(View.VISIBLE);
        userProfileBtn.setVisibility(View.VISIBLE);
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
