package hr.foi.air.foirun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
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

import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.air.database.FoiDatabase;
import hr.foi.air.database.entities.Achievement;
import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.database.entities.User;
import hr.foi.air.foirun.adapter.AktivnostListAdapter;
import hr.foi.air.foirun.data.Sensor;
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.NewSensorEvent;
import hr.foi.air.foirun.events.OnExerciseClick;
import hr.foi.air.foirun.fragments.ProfileAchievementsFragment;
import hr.foi.air.foirun.fragments.ProfileActivityFragment;
import hr.foi.air.foirun.fragments.StartActivityFragment;
import hr.foi.air.foirun.fragments.StopActivityFragment;
import hr.foi.air.foirun.fragments.WeatherActivityFragment;
import hr.foi.air.foirun.util.ActivityTracker;
import hr.foi.air.foirun.util.LocationTracker;
import hr.foi.air.foirun.util.RemoteSensorManager;
import hr.foi.air.foirun.util.SensorTracker;
import hr.foi.air.owf.JSONWeatherParser;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, OnExerciseClick {

    private ActivityTracker mTracker;
    private SensorTracker mSTracker;
    private LocationTracker mLTracker;
    private RemoteSensorManager remoteSensorManager;

    private String start;
    private String stop;

    @BindView(R.id.show_myactivies)
    Button showBtn;

    @BindView(R.id.start_button)
    Button startBtn;

    @BindView(R.id.profile_button)
    Button profileBtn;

    @BindView(R.id.profile_update_button)
    Button updateBtn;

    @BindView(R.id.profile_save_button)
    Button saveUserBtn;

    @BindView(R.id.wear_button)
    ImageButton wearBtn;

    @BindView(R.id.start_buttons)
    LinearLayout startBtns;

    @BindView(R.id.scoreboard)
    ListView scoreboard;

    private SupportMapFragment mapFragment;
    private StartActivityFragment startFragment;
    private StopActivityFragment stopFragment;
    private WeatherActivityFragment weatherActivityFragment;
    private ProfileActivityFragment profileFragment;
    private ProfileAchievementsFragment profileAchievementsFragment;
    private boolean isInListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        start = getResources().getString(R.string.Start_Activity);
        stop = getResources().getString(R.string.Stop_Activity);

        mTracker = new ActivityTracker(this);

        mLTracker = new LocationTracker(MainActivity.this);

        if(mLTracker.canGetLocation()){
            String queryString = String.format(
                    JSONWeatherParser.LATLON_PART,
                    mLTracker.getLatitude(),
                    mLTracker.getLongitude());
            queryString += String.format(JSONWeatherParser.LANGUAGE_PART, getString(R.string.ow_api_language));
            queryString += String.format(JSONWeatherParser.API_KEY_PART, getString(R.string.ow_api_key));
            weatherActivityFragment = (WeatherActivityFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.weatheractivity_fragment);
            weatherActivityFragment.executeTask(queryString);
        } else {
            mLTracker.showSettingsAlert();
        }

        profileFragment = (ProfileActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profileactivity_fragment);

        startFragment = (StartActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.startactivity_fragment);

        stopFragment = (StopActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.stopactivity_fragment);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        profileAchievementsFragment = (ProfileAchievementsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profileaachievements_fragment);



        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        JodaTimeAndroid.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        FoiDatabase.FillActivityTracker();
        FoiDatabase.FillExerciseData();
        //FoiDatabase.FillFakeData();

        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);
        profileFragment.getView().setVisibility(View.INVISIBLE);
        profileAchievementsFragment.getView().setVisibility(View.INVISIBLE);

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
            setupAktivnostList(Aktivnost.getByUserId(uid), false);
        }
    }

    @OnClick(R.id.choose_exercise)
    public void onShowExercises(View view) {
        if (view.getId() == R.id.choose_exercise) {
            //TODO: add filter for exercise if user already complete some
            int uid = getIntent().getIntExtra("uid", 0);
            setupAktivnostList(Aktivnost.getExercises(), true);
        }
    }

    private void setupAktivnostList(List<Aktivnost> aktivnosti, boolean isExercise) {
        AktivnostListAdapter adapter = new AktivnostListAdapter(this, aktivnosti, isExercise);

        scoreboard.setAdapter(adapter);
        scoreboard.setVisibility(View.VISIBLE);
        startFragment.getView().setVisibility(View.INVISIBLE);
        profileFragment.getView().setVisibility(View.INVISIBLE);
        weatherActivityFragment.getView().setVisibility(View.INVISIBLE);
        startBtns.setVisibility(View.INVISIBLE);

        isInListView = true;
    }

    @OnClick(R.id.show_achievements)
    public void onShowAchievements(View view) {
        if (view.getId() == R.id.show_achievements) {
            profileAchievementsFragment.refreshList();
            scoreboard.setVisibility(View.INVISIBLE);
            startFragment.getView().setVisibility(View.INVISIBLE);
            profileFragment.getView().setVisibility(View.INVISIBLE);
            profileAchievementsFragment.getView().setVisibility(View.VISIBLE);
            weatherActivityFragment.getView().setVisibility(View.INVISIBLE);
            startBtns.setVisibility(View.INVISIBLE);

        }
    }

    //Push back button to go back from list view
    @Override
    public void onBackPressed() {

        if(profileFragment.isVisible()){
            if(updateBtn.getText() == "Cancel"){ openDialog();}
            else {
                profileFragment.getView().setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
                weatherActivityFragment.getView().setVisibility(View.VISIBLE);
            }
        } else if (profileAchievementsFragment.isVisible()) {
            startFragment.getView().setVisibility(View.VISIBLE);
            startBtns.setVisibility(View.VISIBLE);
            weatherActivityFragment.getView().setVisibility(View.VISIBLE);
            profileAchievementsFragment.getView().setVisibility(View.INVISIBLE);
        }
        else if (isInListView) {
            scoreboard.setVisibility(View.INVISIBLE);
            startFragment.getView().setVisibility(View.VISIBLE);
            startBtns.setVisibility(View.VISIBLE);


            isInListView = false;
        }
    }

    @OnClick(R.id.profile_button)
    public void onProfileActivity(View view){
        if(view.getId() == R.id.profile_button){
            startFragment.getView().setVisibility(View.INVISIBLE);
            weatherActivityFragment.getView().setVisibility(View.INVISIBLE);
            profileFragment.getView().setVisibility(View.VISIBLE);
            updateBtn.setVisibility(View.VISIBLE);
        }
        int uid = getIntent().getIntExtra("uid",  0);

        profileFragment.getUsername().setText(User.getById(uid).getName());
        profileFragment.getEmail().setText(User.getById(uid).getEmail());
        if(User.getById(uid).getAge() != 0) { profileFragment.getAge().setText(String.valueOf(User.getById(uid).getAge())); }
        if(User.getById(uid).getHeight() != 0) { profileFragment.getHeight().setText(String.valueOf(User.getById(uid).getHeight())); }
        if(User.getById(uid).getWeight() != 0) { profileFragment.getWeight().setText(String.valueOf(User.getById(uid).getWeight())); }
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
                Achievement achievement = new Achievement();
                achievement.setName("10 km covered.");
                achievement.setDate(new Date());
                achievement.setType("");
                achievement.save();
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
            Achievement achievement = new Achievement();
            achievement.setName("New max distance: " + maxDistance + ".");
            achievement.setDate(new Date());
            achievement.setType("Record distance covered in one activity.");
            achievement.save();
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TotalDistanceEvent event) {
        long totalDistance = 0;

        for (Aktivnost a : Aktivnost.getAll()) {
            totalDistance += a.getDistance();
        }


        if (totalDistance >= 50000 && totalDistance < 1000000) {
            //the rookie
            show4thTrophyMessage("The rookie");
            Achievement achievement = new Achievement();
            achievement.setName("The rookie");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();
        } else if (totalDistance >= 1000000 && totalDistance < 200000) {
            //the jogger
            show4thTrophyMessage("The jogger");
            Achievement achievement = new Achievement();
            achievement.setName("The jogger");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();

        } else if (totalDistance >= 2000000 && totalDistance < 300000) {
            //the exhausted
            show4thTrophyMessage("The exhausted");
            Achievement achievement = new Achievement();
            achievement.setName("The exhausted");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();

        } else if (totalDistance >= 300000 && totalDistance < 500000) {
            //the road marshal
            show4thTrophyMessage("The road marshal");
            Achievement achievement = new Achievement();
            achievement.setName("The road marshal");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();

        } else if (totalDistance >= 500000 && totalDistance < 750000) {
            //the inexhaustible
            show4thTrophyMessage("The inexhaustible");
            Achievement achievement = new Achievement();
            achievement.setName("The inexhaustible");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();

        } else if (totalDistance >= 750000 && totalDistance < 1000000) {
            //lord of the road
            show4thTrophyMessage("Lord of the road");
            Achievement achievement = new Achievement();
            achievement.setName("Lord of the road");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();

        } else if (totalDistance >= 1000000) {
            //the unstoppable
            show4thTrophyMessage("The unstoppable");
            Achievement achievement = new Achievement();
            achievement.setName("The unstoppable");
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();

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
            Achievement achievement = new Achievement();
            achievement.setName("5 activities done, total number: " + Aktivnost.getAll().size());
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.save();
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

    @OnClick(R.id.profile_update_button)
    public void onUpdateUser() {

        if (updateBtn.getText().equals("Update")) {
            updateClicked();
        } else {
            cancelClicked();
        }

    }

    public void updateClicked(){
        profileFragment.getEmail().setEnabled(true);
        profileFragment.getAge().setEnabled(true);
        profileFragment.getHeight().setEnabled(true);
        profileFragment.getWeight().setEnabled(true);
        updateBtn.setText("Cancel");
        saveUserBtn.setClickable(true);
    }

    public void cancelClicked(){
        profileFragment.getEmail().setEnabled(false);
        profileFragment.getAge().setEnabled(false);
        profileFragment.getHeight().setEnabled(false);
        profileFragment.getWeight().setEnabled(false);
        updateBtn.setText("Update");

        saveUserBtn.setClickable(false);
    }

    @OnClick(R.id.profile_save_button)
    public void onSaveUserChanges(){
        String msg = "Korisnik je uspješno ažuriran.";
        try {
            User user = User.getById(getIntent().getIntExtra("uid", 0));
            user.setAge(Integer.valueOf(String.valueOf(profileFragment.getAge().getText())));
            user.setEmail(profileFragment.getEmail().getText().toString());
            user.setHeight(Integer.valueOf(String.valueOf(profileFragment.getHeight().getText())));
            user.setWeight(Integer.valueOf(String.valueOf(profileFragment.getWeight().getText())));
            user.update();
        } catch (Exception ex){
            msg = "Došlo je do pogreške!";
            ex.printStackTrace();
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        cancelClicked();
    }

    @OnClick(R.id.start_button)
    public void onStartActivity(View view) {

        if (view.getId() == R.id.start_button) {
            profileFragment.getView().setVisibility(View.INVISIBLE);
            startFragment.getView().setVisibility(View.VISIBLE);
            weatherActivityFragment.getView().setVisibility(View.VISIBLE);

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

    public void openDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Cancel update");
        dialog.setMessage("Are you sure you want to cancel user update?" );
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancelClicked();
                profileFragment.getView().setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
                weatherActivityFragment.getView().setVisibility(View.VISIBLE);
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
        profileFragment.getView().setVisibility(View.INVISIBLE);
        startBtns.setVisibility(View.VISIBLE);
        profileBtn.setVisibility(View.VISIBLE);

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
        weatherActivityFragment.getView().setVisibility(View.VISIBLE);
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
        weatherActivityFragment.getView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMinZoomPreference(16);
        googleMap.setMaxZoomPreference(18);

        //Map is used to show and track movement
        //if somebody wanna look where is moving
        mTracker.setmMap(googleMap);
    }

    private void notifyUSerForNewSensor(Sensor sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        notifyUSerForNewSensor(event.getSensor());
    }

    @Override
    public void onClick(Aktivnost aktivnost) {
        startFragment.setComment(aktivnost.getComment());
        startFragment.setName(aktivnost.getName());
        ActivityType type = ActivityType.getById(aktivnost.getType_id());
        startFragment.setTypeName(type.getName());
        Start(stop);

        Toast.makeText(this, "New Sensor!\n" + aktivnost.getName(), Toast.LENGTH_SHORT).show();
    }
}
