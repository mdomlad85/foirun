package com.example.trophies;

import android.content.Context;
import android.content.res.Resources;

import com.eminayar.panter.PanterDialog;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air.database.entities.Aktivnost;

/**
 * Created by Matej on 06/08/2017.
 */

public class Trophy3 extends Trophy {

    private long totalDistance;

    public Trophy3(List<Aktivnost> aktivnosti) {
        super(aktivnosti);
        totalDistance = 0;
        for (Aktivnost a : aktivnosti) {
            totalDistance += a.getDistance();
        }
    }

    @Override
    public void showDialog(Context context, String text) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage("Congrats, your new record in distance covered.")
                .isCancelable(false)
                .show();
    }

    public boolean isAchieved(){
        return totalDistance >= 5000;
    }

    @Override
    public String getAchivementName() {
        String name = getString(R.string.rookie);
        if (totalDistance >= 5000 && totalDistance < 15000) name = getString(R.string.jogger);
        else if (totalDistance >= 15000 && totalDistance < 30000) name = getString(R.string.exhausted);
        else if (totalDistance >= 30000 && totalDistance < 50000) name = getString(R.string.road_marshal);
        else if (totalDistance >= 50000 && totalDistance < 75000)  name = getString(R.string.inexhaustible);
        else if (totalDistance >= 75000 && totalDistance < 100000)  name = getString(R.string.road_lord);
        else if (totalDistance >= 100000) name = getString(R.string.unstoppable);

        return name;
    }

    @Override
    public String getText() {
        return  getAchivementName();
    }
}
