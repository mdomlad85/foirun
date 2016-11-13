package hr.foi.air.database.entities;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hr.foi.air.database.core.DbModels;
import hr.foi.air.database.core.SuperEntity;

public class LocationEntity extends SuperEntity {

    private Double mDistance;
    private Long mElapsed;

    public LocationEntity() {
        super();
    }

    private LocationEntity(Cursor c, LocationEntity lastLocation) {
        super();
        toContentValues(c);

        // Compute distance and elapsed
        Double distance = 0.0;
        Long elapsed = 0L;
        if (lastLocation != null) {
            //First point is zero
            int type = this.getType();
            distance = lastLocation.getDistance();
            elapsed = lastLocation.getElapsed();
            switch (type) {
                case DbModels.LOCATION.TYPE_START:
                case DbModels.LOCATION.TYPE_END:
                case DbModels.LOCATION.TYPE_RESUME:
                    break;
                case DbModels.LOCATION.TYPE_PAUSE:
                case DbModels.LOCATION.TYPE_GPS:
                    float res[] = {
                            0
                    };
                    Location.distanceBetween(lastLocation.getLatitude(),
                            lastLocation.getLongitude(), this.getLatitude(), this.getLongitude(),
                            res);
                    distance += res[0];
                    elapsed += this.getTime() - lastLocation.getTime();
                    break;
            }
        }
        mDistance = distance;
        mElapsed = elapsed;
    }

    public static class LocationList<E> implements Iterable<E> {
        LocationIterator iter;
        final long mID;
        final SQLiteDatabase mDB;

        public LocationList(SQLiteDatabase mDB, long mID) {
            this.mID = mID;
            this.mDB = mDB;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<E> iterator() {
            iter = new LocationIterator(this.mID, this.mDB);
            return iter;
        }

        public int getCount() {
            return iter == null ? 0 : iter.getCount();
        }

        public void close() {
            if (iter != null) {iter.close();}
        }

        private class LocationIterator implements Iterator<E> {
            private LocationIterator(long mID, SQLiteDatabase mDB) {
                c = mDB.query(DbModels.LOCATION.TABLE, from, DbModels.LOCATION.ACTIVITY_ID + " == " + mID,
                        null, null, null, DbModels.PRIMARY_KEY, null);
                if (!c.moveToFirst()) {
                    c.close();
                }
            }

            final String[] from = new String[]{
                    DbModels.LOCATION.LATITUDE,
                    DbModels.LOCATION.LONGITUDE,
                    DbModels.LOCATION.TYPE,
                    DbModels.LOCATION.TIME,
                    DbModels.LOCATION.LAP,
                    DbModels.LOCATION.HR
            };
            Cursor c = null;
            E prev = null;

            public int getCount() {
                return c.getCount();
            }

            public void close() {
                if (!c.isClosed()) {
                    c.close();
                }
            }

            @Override
            public boolean hasNext() {
                return !c.isClosed() && !c.isLast();
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                c.moveToNext();
                prev = (E)new LocationEntity(c, (LocationEntity)prev);
                if (c.isLast()) {
                    c.close();
                }
                return prev;
            }

            @Override
            public void remove() {
                next();
            }
        }
    }

    /**
     * Id of the activity the location point belongs to
     */
    public void setActivityId(Long value) {
        values().put(DbModels.LOCATION.ACTIVITY_ID, value);
    }

    public Long getActivityId() {
        if (values().containsKey(DbModels.LOCATION.ACTIVITY_ID)) {
            return values().getAsLong(DbModels.LOCATION.ACTIVITY_ID);
        }
        return null;
    }

    /**
     * Lap number of the activity the location point belongs to
     */
    public void setLap(Integer value) {
        values().put(DbModels.LOCATION.LAP, value);
    }

    public Integer getLap() {
        if (values().containsKey(DbModels.LOCATION.LAP)) {
            return values().getAsInteger(DbModels.LOCATION.LAP);
        }
        return null;
    }

    /**
     * Type of the location point
     */
    public void setType(Integer value) {
        values().put(DbModels.LOCATION.TYPE, value);
    }

    public Integer getType() {
        if (values().containsKey(DbModels.LOCATION.TYPE)) {
            return values().getAsInteger(DbModels.LOCATION.TYPE);
        }
        return null;
    }

    /**
     * The moment in time when the location point was recorded
     */
    public void setTime(Long value) {
        values().put(DbModels.LOCATION.TIME, value);
    }

    public Long getTime() {
        if (values().containsKey(DbModels.LOCATION.TIME)) {
            return values().getAsLong(DbModels.LOCATION.TIME);
        }
        return null;
    }

    /**
     * Longitude of the location
     */
    public void setLongitude(Double value) {
        values().put(DbModels.LOCATION.LONGITUDE, value);
    }

    public Double getLongitude() {
        if (values().containsKey(DbModels.LOCATION.LONGITUDE)) {
            return values().getAsDouble(DbModels.LOCATION.LONGITUDE);
        }
        return null;
    }

    /**
     * Latitude of the location
     */
    public void setLatitude(Double value) {
        values().put(DbModels.LOCATION.LATITUDE, value);
    }

    public Double getLatitude() {
        if (values().containsKey(DbModels.LOCATION.LATITUDE)) {
            return values().getAsDouble(DbModels.LOCATION.LATITUDE);
        }
        return null;
    }

    /**
     * Distance of the location
     */

    public Double getDistance() {
        return mDistance;
    }

    /**
     * Elapsed time in ms, excluding pauses
     */
    public Long getElapsed() {
        return mElapsed;
    }


    /**
     * Accuracy of the location
     */
    private void setAccuracy(Float value) {
        values().put(DbModels.LOCATION.ACCURANCY, value);
    }

    public Float getAccuracy() {
        if (values().containsKey(DbModels.LOCATION.ACCURANCY)) {
            return values().getAsFloat(DbModels.LOCATION.ACCURANCY);
        }
        return null;
    }

    /**
     * Altitude of the location
     */
    public void setAltitude(Double value) {
        values().put(DbModels.LOCATION.ALTITUDE, value);
    }

    public Double getAltitude() {
        if (values().containsKey(DbModels.LOCATION.ALTITUDE)) {
            return values().getAsDouble(DbModels.LOCATION.ALTITUDE);
        }
        return null;
    }

    /**
     * Speed of the location
     */
    public void setSpeed(Float value) {
        values().put(DbModels.LOCATION.SPEED, value);
    }

    public Float getSpeed() {
        if (values().containsKey(DbModels.LOCATION.SPEED)) {
            return values().getAsFloat(DbModels.LOCATION.SPEED);
        }
        return null;
    }

    /**
     * Bearing of the location
     */
    private void setBearing(Float value) {
        values().put(DbModels.LOCATION.BEARING, value);
    }

    public Float getBearing() {
        if (values().containsKey(DbModels.LOCATION.BEARING)) {
            return values().getAsFloat(DbModels.LOCATION.BEARING);
        }
        return null;
    }

    /**
     * HR at the location
     */
    public void setHr(Integer value) {
        values().put(DbModels.LOCATION.HR, value);
    }

    public Integer getHr() {
        if (values().containsKey(DbModels.LOCATION.HR)) {
            return values().getAsInteger(DbModels.LOCATION.HR);
        }
        return null;
    }

    /**
     * Cadence at the location
     */
    private void setCadence(Integer value) {
        values().put(DbModels.LOCATION.CADENCE, value);
    }

    public Integer getCadence() {
        if (values().containsKey(DbModels.LOCATION.CADENCE)) {
            return values().getAsInteger(DbModels.LOCATION.CADENCE);
        }
        return null;
    }

    @Override
    protected List<String> getValidColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(DbModels.PRIMARY_KEY);
        columns.add(DbModels.LOCATION.ACTIVITY_ID);
        columns.add(DbModels.LOCATION.LAP);
        columns.add(DbModels.LOCATION.TYPE);
        columns.add(DbModels.LOCATION.TIME);
        columns.add(DbModels.LOCATION.LATITUDE);
        columns.add(DbModels.LOCATION.LONGITUDE);
        columns.add(DbModels.LOCATION.ACCURANCY);
        columns.add(DbModels.LOCATION.ALTITUDE);
        columns.add(DbModels.LOCATION.SPEED);
        columns.add(DbModels.LOCATION.BEARING);
        columns.add(DbModels.LOCATION.HR);
        columns.add(DbModels.LOCATION.CADENCE);
        return columns;
    }

    @Override
    protected String getTableName() {
        return DbModels.LOCATION.TABLE;
    }

    @Override
    protected String getNullColumnHack() {
        return null;
    }
}

