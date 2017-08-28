package com.example.trophies;

import android.content.Context;
import android.content.res.Resources;

import com.eminayar.panter.PanterDialog;

import java.util.Date;
import java.util.List;

import hr.foi.air.database.entities.Achievement;
import hr.foi.air.database.entities.Aktivnost;

/**
 * Created by Matej on 06/08/2017.
 */

public class Trophy2 extends Trophy {
    private double currentMax;

    private double maxDistance;

    public Trophy2(List<Aktivnost> aktivnosti) {
        super(aktivnosti);
        for (int i = 0; i < aktivnosti.size() -1; i++) {
            double currentDistance = aktivnosti.get(i).getDistance();
            if (currentDistance >= maxDistance) {
                maxDistance = currentDistance;
            }
        }
        currentMax = aktivnosti.get(aktivnosti.size() - 1).getDistance();

    }
    //New Max distance


    @Override
    public void showDialog(Context context, String text) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage("Congrats on 5 workouts")
                .isCancelable(false)
                .show();
    }

    @Override
    public boolean isAchieved() {
        return currentMax > maxDistance;
    }

    @Override
    public String getAchivementName() {
        return String.format(getString(R.string.novi_rekord), maxDistance / 1000);
    }

    @Override
    public String getText() {
        return getString(R.string.novi_rekord_opis);
    }

    public double getMaxDistance() {
        return maxDistance;
    }
}
