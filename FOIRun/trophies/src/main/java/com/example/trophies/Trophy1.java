package com.example.trophies;

import android.content.Context;

import com.eminayar.panter.PanterDialog;

/**
 * Created by Matej on 06/08/2017.
 */

public class Trophy1 implements Trophy {

    //10 km run
    @Override
    public void showDialog(Context context, String text) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage("Congrats on running 10 km")
                .isCancelable(false)
                .show();
    }
}
