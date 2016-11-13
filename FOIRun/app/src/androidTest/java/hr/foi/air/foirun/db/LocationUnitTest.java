package hr.foi.air.foirun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.Objects;

import hr.foi.air.database.core.DbModels;
import hr.foi.air.database.entities.LocationEntity;
import hr.foi.air.database.helpers.DbHelper;

import static org.junit.Assert.*;

public class LocationUnitTest {

    private LocationEntity fillEntity() {
        LocationEntity locEntity = new LocationEntity();
        locEntity.setActivityId(1L);
        locEntity.setTime(3600000L);
        locEntity.setLatitude(45.748714);
        locEntity.setLongitude(16.613118);
        locEntity.setLap(10);
        locEntity.setType(DbModels.LOCATION.TYPE_START);

        return locEntity;
    }

    public void insert_Location() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
        final long insert = fillEntity().insert(DbHelper.getWritableDatabase(ctx));
        assertTrue(insert != -1);
    }

    @Test
    public void update_Location() throws Exception {
        insert_Location();
        long id = 1;
        double alt = Math.random() * 100;
        Context ctx = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase mDB = DbHelper.getWritableDatabase(ctx);
        LocationEntity le = new LocationEntity();
        //read
        le.readByPrimaryKey(mDB, 1);
        le.setAltitude(alt);
        //update
        le.update(mDB);
        //read again for check
        le.readByPrimaryKey(mDB, id);
        assertEquals(le.getAltitude(), alt, 0);
    }

    @Test
    public void select_Location() throws Exception {
        insert_Location();
        Context ctx = InstrumentationRegistry.getTargetContext();
        final SQLiteDatabase mDB = DbHelper.getReadableDatabase(ctx);
        LocationEntity.LocationList<LocationEntity> ll = new LocationEntity.LocationList<>(mDB, 1);

        LocationEntity le = new LocationEntity();
        le.readByPrimaryKey(mDB, 1);

        boolean isValid = false;
        while (ll.iterator().hasNext()){
            LocationEntity l = ll.iterator().next();
            if(Objects.equals(le.getLatitude(), l.getLatitude())){
                isValid = true;
                break;
            }
        }
        assertTrue(isValid);
    }
}
