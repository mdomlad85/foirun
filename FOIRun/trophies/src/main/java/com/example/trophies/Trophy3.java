package com.example.trophies;

import android.content.Context;

import com.eminayar.panter.PanterDialog;

/**
 * Created by Matej on 06/08/2017.
 */

public class Trophy3 implements Trophy {
    @Override
    public void showDialog(Context context) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage("Congrats your record distance covered.")
                .isCancelable(false)
                .show();
    }
}
