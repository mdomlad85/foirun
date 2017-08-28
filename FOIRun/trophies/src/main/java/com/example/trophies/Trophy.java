package com.example.trophies;

import android.content.Context;
import android.content.res.Resources;

import java.util.List;

import hr.foi.air.database.entities.Aktivnost;

/**
 * Created by mitz on 28.08.17..
 */

public abstract class Trophy {
    private final List<Aktivnost> aktivnosti;

    public Trophy(List<Aktivnost> aktivnosti){
        this.aktivnosti = aktivnosti;
    }

    protected String getString(int id){
        return Resources.getSystem().getString(id);
    }

    abstract public void showDialog(Context context, String text);

    abstract public boolean isAchieved();

    abstract public String getAchivementName();

    abstract public String getText();
}
