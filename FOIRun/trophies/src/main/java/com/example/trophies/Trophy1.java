package com.example.trophies;

import android.content.Context;
import android.content.res.Resources;

import com.eminayar.panter.PanterDialog;

import java.util.List;

import hr.foi.air.database.entities.Aktivnost;

/**
 * Created by Matej on 06/08/2017.
 */

public class Trophy1 extends Trophy {

    private final double lastDistance;

    public Trophy1(List<Aktivnost> aktivnosti) {
        super(aktivnosti);
        this.lastDistance = aktivnosti.get(aktivnosti.size() - 1).getDistance();
    }

    //10 km run
    @Override
    public void showDialog(Context context, String text) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage("Congrats on running 10 km")
                .isCancelable(false)
                .show();
    }

    @Override
    public boolean isAchieved() {
        return lastDistance >= 10000;
    }

    @Override
    public String getAchivementName() {
        return String.format(getString(R.string.km_covered), 10);
    }

    @Override
    public String getText() {
        return String.format(getString(R.string.km_covered), 10);
    }
}
