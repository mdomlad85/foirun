package hr.foi.air.foirun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.air.database.FoiDatabase;
import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.database.entities.Aktivnost;
import hr.foi.air.database.entities.Location;
import hr.foi.air.foirun.util.ActivityTracker;

public class MainActivity extends AppCompatActivity {

    private ActivityTracker mTracker;

    @BindView(R.id.start_button)
    Button startBtn;

    @BindView(R.id.wear_button)
    ImageButton wearBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        ButterKnife.bind(this);
        JodaTimeAndroid.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        FoiDatabase.FillActivityTracker();

        List<Location> locations = Location.getAll();
        List<ActivityType> types = ActivityType.getAll();
        List<Aktivnost> act = Aktivnost.getAll();

        mTracker = new ActivityTracker(this);
    }

    @OnClick(R.id.start_button)
    public void onGpsStartClick(View view){

        if(view.getId() == R.id.start_button){

            mTracker.Start("Test", 1, false);

            startBtn.setVisibility(View.INVISIBLE);
        }



    }

    @OnClick(R.id.wear_button)
    public void onGpsStopClick(View view){

        if(view.getId() == R.id.wear_button){

            mTracker.Stop();

            startBtn.setVisibility(View.VISIBLE);
        }

    }
}
