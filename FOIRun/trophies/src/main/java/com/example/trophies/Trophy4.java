package com.example.trophies;

import android.content.Context;

import com.eminayar.panter.PanterDialog;

/**
 * Created by Matej on 21/08/2017.
 */

public class Trophy4 implements Trophy {
    @Override
    public void showDialog(Context context, String text) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage(text)
                .isCancelable(false)
                .show();
    }
}
