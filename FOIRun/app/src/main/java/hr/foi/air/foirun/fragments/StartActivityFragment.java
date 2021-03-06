package hr.foi.air.foirun.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.air.database.entities.ActivityType;
import hr.foi.air.foirun.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartActivityFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.name)
    AutoCompleteTextView nameTxt;

    @BindView(R.id.comment)
    EditText commentTxt;

    @BindView(R.id.activity_types)
    Spinner typesDdl;

    @BindView(R.id.list_buttons)
    RelativeLayout listButtons;

    protected String mTypeName;

    public StartActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.start_activity, container, false);

        ButterKnife.bind(this, view);

        BindData();

        // Inflate the layout for this fragment
        return view;
    }


    public void BindData() {

        typesDdl.setOnItemSelectedListener(this);

        //get strings of first item
        mTypeName = String.valueOf(typesDdl.getItemAtPosition(0));

    }

    public String getName() {
        return nameTxt.getText().toString();
    }

    public int getTypeId() {
        ActivityType activityType = ActivityType.getByName(mTypeName);

        return activityType.getId();
    }

    public String getComment() {
        String comment = commentTxt.getText().toString();

        return comment == null ? "" : comment;
    }

    public void setName(String text) {
        nameTxt.setText(text);
    }

    public void setTypeName(String typeName) {
        mTypeName = typeName;
    }

    public void setComment(String comment) {
        commentTxt.setText(comment);
    }

    public boolean isValid() {
        String name = nameTxt.getText().toString();

        return name != null && name.length() > 0;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mTypeName = String.valueOf(typesDdl.getSelectedItem());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg) {

    }

    public void showListButtons(boolean show){
        listButtons.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void ClearForm() {
        commentTxt.setText("");
        nameTxt.setText("");
        typesDdl.setSelection(0);
    }
}
