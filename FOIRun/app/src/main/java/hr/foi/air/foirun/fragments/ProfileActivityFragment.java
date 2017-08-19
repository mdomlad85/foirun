package hr.foi.air.foirun.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import hr.foi.air.foirun.R;

/**
 * Created by Tomek on 18.8.2017..
 */

public class ProfileActivityFragment extends Fragment {

    public ProfileActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_activity, container, false);

        ButterKnife.bind(this, view);

        // Inflate the layout for this fragment
        return view;
    }


}
