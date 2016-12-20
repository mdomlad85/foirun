package hr.foi.air.foirun.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

public class LocationUnitTest {

    public void insert_Location() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void update_Location() throws Exception {
        insert_Location();
        long id = 1;
        double alt = Math.random() * 100;
        Context ctx = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void select_Location() throws Exception {
        insert_Location();
        Context ctx = InstrumentationRegistry.getTargetContext();
    }
}
