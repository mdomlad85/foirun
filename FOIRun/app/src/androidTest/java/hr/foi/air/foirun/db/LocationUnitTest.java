package hr.foi.air.foirun.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.Assert;
import org.junit.Test;

import hr.foi.air.database.entities.User;

public class LocationUnitTest {

    @Test
    public void insert_Location() throws Exception {
        Context ctx = InstrumentationRegistry.getTargetContext();
        FlowManager.init(new FlowConfig.Builder(ctx).build());
        User user = new User("mkapustic", "mkapustic@gmail.com", "marko985", false, null, null, 0, 0, 0);
        long insert = user.insert();

        Assert.assertTrue(insert > 0);
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
