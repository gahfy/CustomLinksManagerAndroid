package net.gahfy.devtools.customlink;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@android.support.test.filters.SmallTest
public class DevToolsApplicationInstrumentedTest {
    public void get_database() throws Exception {
        DevToolsApplication devToolsApplication = (DevToolsApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        SQLiteDatabase sqliteDatabase1 = devToolsApplication.getDatabase();
        SQLiteDatabase sqliteDatabase2 = devToolsApplication.getDatabase();
        Assert.assertTrue("Check singleton on DevToolsApplication.getDatabase()", sqliteDatabase1 == sqliteDatabase2);
    }
}
