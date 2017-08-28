package com.example.trophies;

import android.content.Context;
import android.content.res.Resources;

import com.eminayar.panter.PanterDialog;

import java.util.List;

import hr.foi.air.database.entities.Aktivnost;

/**
 * Created by Matej on 21/08/2017.
 */

public class Trophy4 extends Trophy {
    private final int trenutnoAktivnosti;

    public Trophy4(List<Aktivnost> aktivnosti) {
        super(aktivnosti);
        trenutnoAktivnosti = aktivnosti.size();
    }

    @Override
    public void showDialog(Context context, String text) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage(text)
                .isCancelable(false)
                .show();
    }

    @Override
    public boolean isAchieved() {
        return trenutnoAktivnosti % 5 == 0 && trenutnoAktivnosti > 0;
    }

    @Override
    public String getAchivementName() {
        return String.format(getString(R.string.broj_aktivnosti), trenutnoAktivnosti);
    }

    @Override
    public String getText() {
        return getAchivementName();
    }
}
