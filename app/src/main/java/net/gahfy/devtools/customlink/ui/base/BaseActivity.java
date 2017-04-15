package net.gahfy.devtools.customlink.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import net.gahfy.devtools.customlink.BuildConfig;
import net.gahfy.devtools.customlink.DevToolsApplication;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    @Override
    public DevToolsApplication getDevToolsApplication(){
        return (DevToolsApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        enableStrictMode();
        super.onCreate(savedInstanceState);
    }

    private void enableStrictMode(){
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                builder.detectLeakedClosableObjects();
            }
            builder.penaltyLog()
                    .penaltyDeath()
                    .build();
        }
    }

    @Override
    public void setTitle(String title){
        super.setTitle(title);
    }
}
