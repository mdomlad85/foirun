package hr.foi.air.foirun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import hr.foi.air.foirun.events.BusProvider;
import hr.foi.air.foirun.events.OnExerciseClick;
import hr.foi.air.foirun.fragments.ProfileAchievementsFragment;
import hr.foi.air.foirun.fragments.ProfileActivityFragment;
import hr.foi.air.foirun.fragments.StartActivityFragment;
import hr.foi.air.foirun.fragments.StopActivityFragment;
import hr.foi.air.foirun.fragments.WeatherActivityFragment;
import hr.foi.air.foirun.util.ActivityTracker;
import hr.foi.air.foirun.util.LocationTracker;
import hr.foi.air.owf.JSONWeatherParser;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, OnExerciseClick {

    private ActivityTracker mTracker;
    private LocationTracker mLTracker;

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
        int uid = getIntent().getIntExtra("uid", 0);
        mTracker = new ActivityTracker(this, uid);
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
        profileAchievementsFragment.setUid(uid);



        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        JodaTimeAndroid.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        FoiDatabase.FillActivityTracker();
        FoiDatabase.FillExerciseData();

        mapFragment.getView().setVisibility(View.INVISIBLE);
        stopFragment.getView().setVisibility(View.INVISIBLE);
        profileFragment.getView().setVisibility(View.INVISIBLE);
        profileAchievementsFragment.getView().setVisibility(View.INVISIBLE);

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
            setupAktivnostList(Aktivnost.getExercises(), true);
        }
    }

    private void setupAktivnostList(List<Aktivnost> aktivnosti, boolean isExercise) {
        final AktivnostListAdapter adapter = new AktivnostListAdapter(this, aktivnosti, isExercise);
        scoreboard.setAdapter(adapter);
        scoreboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
            }
        });
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
            if(updateBtn.getText().equals(getString(R.string.cancel_update))){ openDialog();}
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
            weatherActivityFragment.getView().setVisibility(View.VISIBLE);
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
        profileFragment.addUser(User.getById(uid));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveDistanceEvent event) {/* Do something */
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSettings.edit();
        double km = 0;

        int uid = getIntent().getIntExtra("uid", 0);
        List<Aktivnost> aktivnosti = Aktivnost.getByUserId(uid);

        for (Aktivnost a : aktivnosti) {
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
                achievement.setName(String.format(getString(R.string.km_covered), 10));
                achievement.setDate(new Date());
                achievement.setType("");
                achievement.setUser_id(uid);
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

        int uid = getIntent().getIntExtra("uid", 0);
        List<Aktivnost> aktivnosti = Aktivnost.getByUserId(uid);


        for (Aktivnost a : aktivnosti) {
            if (a.getDistance() >= maxDistance) {
                maxDistance = (long) a.getDistance();
            }
        }
        editor.putLong("max_distance", maxDistance);
        editor.apply();

        if (maxDistance > maxSaved) {
            show3rdTrophyMessage();
            Achievement achievement = new Achievement();
            achievement.setName(String.format(getString(R.string.novi_rekord), maxDistance / 1000));
            achievement.setDate(new Date());
            achievement.setType(getString(R.string.novi_rekord_opis));
            achievement.setUser_id(uid);
            achievement.save();
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TotalDistanceEvent event) {
        long totalDistance = 0;

        int uid = getIntent().getIntExtra("uid", 0);
        List<Aktivnost> aktivnosti = Aktivnost.getByUserId(uid);

        for (Aktivnost a : aktivnosti) {
            totalDistance += a.getDistance();
        }

        String name = getString(R.string.rookie);
        if (totalDistance >= 5000 && totalDistance < 15000) name = getString(R.string.jogger);
        else if (totalDistance >= 15000 && totalDistance < 30000) name = getString(R.string.exhausted);
        else if (totalDistance >= 30000 && totalDistance < 50000) name = getString(R.string.road_marshal);
        else if (totalDistance >= 50000 && totalDistance < 75000)  name = getString(R.string.inexhaustible);
        else if (totalDistance >= 75000 && totalDistance < 100000)  name = getString(R.string.road_lord);
        else if (totalDistance >= 100000) name = getString(R.string.unstoppable);

        show4thTrophyMessage(name);
        Achievement achievement = new Achievement();
        achievement.setName(name);
        achievement.setDate(new Date());
        achievement.setType(name);
        achievement.setUser_id(uid);
        achievement.save();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NumberOfActivitiesEvent event) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSettings.edit();

        int uid = getIntent().getIntExtra("uid", 0);
        int trenutnoAktivnosti = Aktivnost.getByUserId(uid).size();
        if (trenutnoAktivnosti % 5 == 0 && trenutnoAktivnosti > 0) {
            show2stTrophyMessage();
            editor.putLong("activities_number", trenutnoAktivnosti);
            editor.apply();
            Achievement achievement = new Achievement();
            achievement.setName(String.format(getString(R.string.broj_aktivnosti), trenutnoAktivnosti));
            achievement.setDate(new Date());
            achievement.setType("");
            achievement.setUser_id(uid);
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

        if (updateBtn.getText().equals(getString(R.string.action_update))) {
            updateClicked();
        } else {
            cancelClicked();
        }

    }

    public void updateClicked(){
        profileFragment.setEnabled(true);
        updateBtn.setText(getString(R.string.action_cancel));
        saveUserBtn.setClickable(true);
    }

    public void cancelClicked(){
        profileFragment.setEnabled(false);
        profileFragment.setValues();
        updateBtn.setText(getString(R.string.action_update));
        saveUserBtn.setClickable(false);
    }

    @OnClick(R.id.profile_save_button)
    public void onSaveUserChanges(){
        String msg = getString(R.string.update_user_success);
        try {
            profileFragment.saveUser();
        } catch (Exception ex){
            msg = getString(R.string.update_user_error);
            ex.printStackTrace();
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        cancelClicked();
    }

    @OnClick(R.id.start_button)
    public void onStartActivity(View view) {

        if (view.getId() == R.id.start_button) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                    String.format(getString(R.string.spremljena_aktivnost), mTracker.getAktivnost().getName()),
                    Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.delete_activity)
    public void onDeleteActivity(View view) {

        if (view.getId() == R.id.delete_activity) {

            PrepearePodium();

            mTracker.getAktivnost().delete();

            Toast.makeText(this,
                    String.format("Aktivnost \"%s\" je obrisana", mTracker.getAktivnost().getName()),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void openDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.cancel_update));
        dialog.setMessage(getString(R.string.confirm_cancel));
        dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancelClicked();
                profileFragment.getView().setVisibility(View.INVISIBLE);
                startFragment.getView().setVisibility(View.VISIBLE);
                weatherActivityFragment.getView().setVisibility(View.VISIBLE);
            }
        })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
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
        startFragment.showListButtons(true);
        profileBtn.setVisibility(View.VISIBLE);

    }

    private void Stop(String btnText) {

        startBtns.setVisibility(View.INVISIBLE);
        startFragment.showListButtons(false);
        startBtn.setText(btnText);
        mTracker.Stop();
        BusProvider.getInstance().unregister(this);

        stopFragment.setActivityTxt(mTracker.getAktivnost().getType_id());
        stopFragment.setDistanceTxt(mTracker.getAktivnost().getDistance());
        stopFragment.setTimeTxt(mTracker.getAktivnost().getTime());

        stopFragment.getView().setVisibility(View.VISIBLE);
    }

    private void Start(String stop) {

        if (!startFragment.isValid()) {
            Toast.makeText(this, "Morate unijeti naslov.", Toast.LENGTH_LONG).show();
            return;
        }

        startBtn.setText(stop);

        mTracker.Start(startFragment.getName(),
        startFragment.getComment(), startFragment.getTypeId());

        BusProvider.getInstance().register(this);

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

    @Override
    public void onClick(Aktivnost aktivnost) {
        startFragment.setComment(aktivnost.getComment());
        startFragment.setName(aktivnost.getName());
        ActivityType type = ActivityType.getById(aktivnost.getType_id());
        startFragment.setTypeName(type.getName());
        mTracker.setExercise(aktivnost);
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        startBtns.setVisibility(View.VISIBLE);
        scoreboard.setVisibility(View.INVISIBLE);
        Start(stop);
    }
}
