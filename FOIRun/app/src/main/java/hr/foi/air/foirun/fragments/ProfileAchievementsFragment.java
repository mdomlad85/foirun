package hr.foi.air.foirun.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import hr.foi.air.database.entities.Achievement;
import hr.foi.air.foirun.R;
import hr.foi.air.foirun.adapter.AchievementsListAdapter;

/**
 * Created by Matej on 22/08/2017.
 */

public class ProfileAchievementsFragment extends Fragment {

    RecyclerView rvAchievements;
    private int _uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievements_fragment, container, false);

        rvAchievements = (RecyclerView) view.findViewById(R.id.rvAchievements);
        AchievementsListAdapter achievementsListAdapter = new AchievementsListAdapter(Achievement.getByUserId(_uid), getContext());
        rvAchievements.setAdapter(achievementsListAdapter);
        rvAchievements.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void refreshList() {
        AchievementsListAdapter achievementsListAdapter = new AchievementsListAdapter(Achievement.getByUserId(_uid), getContext());
        rvAchievements.setAdapter(achievementsListAdapter);

        rvAchievements.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setUid(int uid) {
        this._uid = uid;
    }
}
