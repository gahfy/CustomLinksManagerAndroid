package net.gahfy.devtools.customlink;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DevToolsApplicationInstrumentatedTests {
    @Test
    public void get_database() throws Exception {
        DevToolsApplication devToolsApplication = (DevToolsApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        SQLiteDatabase sqLiteDatabase = devToolsApplication.getDatabase();
        Assert.assertEquals("Test SQLiteDatabase DevToolsApplication.getDatabase()", 1, sqLiteDatabase.getVersion());
    }
}
