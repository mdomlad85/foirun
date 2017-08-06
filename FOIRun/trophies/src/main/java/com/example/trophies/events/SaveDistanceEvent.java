package com.example.trophies.events;

/**
 * Created by Matej on 06/08/2017.
 */

public class SaveDistanceEvent {

    private long distanceRun;

    public SaveDistanceEvent(long distance) {
        this.distanceRun = distance;
    }

    public long getDistanceRun() {
        return distanceRun;
    }

    public void setDistanceRun(long distanceRun) {
        this.distanceRun = distanceRun;
    }
}
