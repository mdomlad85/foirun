package hr.foi.air.foirun.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.air.database.entities.User;
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
    private User user;

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

    public void addUser(User user) {
        this.user = user;
        setValues();
    }

    public void setEnabled(boolean enabled) {
        email.setEnabled(enabled);
        age.setEnabled(enabled);
        height.setEnabled(enabled);
        weight.setEnabled(enabled);
    }

    public void saveUser() {
        user.setAge(Integer.valueOf(String.valueOf(age.getText())));
        user.setEmail(email.getText().toString());
        user.setHeight(Integer.valueOf(String.valueOf(height.getText())));
        user.setWeight(Integer.valueOf(String.valueOf(weight.getText())));
        user.update();
    }

    public void setValues() {
        username.setText(user.getName());
        email.setText(user.getEmail());
        if(user.getAge() != 0) { age.setText(String.valueOf(user.getAge())); }
        if(user.getHeight() != 0) { height.setText(String.valueOf(user.getHeight())); }
        if(user.getWeight() != 0) { weight.setText(String.valueOf(user.getWeight())); }
    }
}
