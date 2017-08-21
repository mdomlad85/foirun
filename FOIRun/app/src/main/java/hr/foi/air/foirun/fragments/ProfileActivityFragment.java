package hr.foi.air.foirun.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.air.foirun.R;

/**
 * Created by Tomek on 18.8.2017..
 */

public class ProfileActivityFragment extends Fragment {


    @BindView(R.id.profile_username)
    EditText username;

    @BindView(R.id.profile_email)
    EditText email;

    @BindView(R.id.profile_age)
    EditText age;

    @BindView(R.id.profile_height)
    EditText height;

    @BindView(R.id.profile_weight)
    EditText weight;

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

    public EditText getUsername() {
        return username;
    }

    public EditText getEmail() {
        return email;
    }

    public EditText getAge() {
        return age;
    }

    public EditText getHeight() {
        return height;
    }

    public EditText getWeight() {
        return weight;
    }
}
