package hr.foi.air.foirun.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.foirun.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StopActivityFragment extends Fragment {

    @BindView(R.id.distance)
    TextView distanceTxt;

    @BindView(R.id.activity)
    TextView activityTxt;

    @BindView(R.id.time)
    TextView timeTxt;

    public StopActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stop_activity, container, false);

        ButterKnife.bind(this, view);

        // Inflate the layout for this fragment
        return view;
    }

    public void setDistanceTxt(double distance) {
        this.distanceTxt.setText(String.format( "Distance:\t\t %.2f m", distance ));
    }

    public void setTimeTxt(long milis) {
        int seconds = (int) (milis / 1000) % 60 ;
        int minutes = (int) ((milis / (1000*60)) % 60);
        int hours   = (int) ((milis / (1000*60*60)) % 24);

        this.timeTxt.setText( String.format( "Time:\t\t %02d h %02d min %02d sec", hours, minutes, seconds ) );
    }

    public void setActivityTxt(int typeId) {
        ActivityType type = ActivityType.getById(typeId);

        this.activityTxt.setText(String.format( "Activity:\t\t %s", type.getName() ));
    }
}
