package com.example.trophies;

import android.content.Context;

import com.eminayar.panter.PanterDialog;

/**
 * Created by mariofil on 06/08/2017.
 */

public class Trophy2 implements Trophy {
    //5 workouts done


    @Override
    public void showDialog(Context context) {
        new PanterDialog(context)
                .setTitle("Congratulations")
                .setMessage("Congrats on 5 workouts")
                .isCancelable(false)
                .show();
    }
}
