package hr.foi.air.foirun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import hr.foi.air.database.core.DbModels;
import hr.foi.air.database.entities.LocationEntity;
import hr.foi.air.database.helpers.DbHelper;

import static org.junit.Assert.*;

public class LocationUnitTest {

    private LocationEntity get_some_entity(long id) {
        LocationEntity locEntity = new LocationEntity();
        locEntity.setActivityId(1L);
        locEntity.setTime(3600000L);
        locEntity.setLatitude(45.748714);
        locEntity.setLongitude(16.613118);
        locEntity.setLap(10);
        locEntity.setType(DbModels.LOCATION.TYPE_START);
        if(id != -1) locEntity.setId(id);

        return locEntity;
    }

    @Test
    public void insert_Location() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
        final long insert = get_some_entity(-1).insert(DbHelper.getWritableDatabase(ctx));
        assertTrue(insert != -1);
    }

    @Test
    public void update_Location() throws Exception {
        LocationEntity loc = get_some_entity(1);
        loc.setAltitude(25.11);
        Context ctx = InstrumentationRegistry.getTargetContext();
        final long update = loc.update(DbHelper.getWritableDatabase(ctx));
        assertTrue(update != -1);
    }

    @Test
    public void select_Location() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
        final SQLiteDatabase db = DbHelper.getReadableDatabase(ctx);
        LocationEntity.LocationList<LocationEntity> ll = new LocationEntity.LocationList<>(db, 1);
        assertNotNull(ll.iterator());
        assertTrue(ll.getCount() > 0);
        ll.close();
    }
}
