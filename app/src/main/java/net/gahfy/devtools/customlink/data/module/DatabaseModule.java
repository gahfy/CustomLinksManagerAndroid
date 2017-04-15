package net.gahfy.devtools.customlink.data.module;

import android.support.annotation.NonNull;

import net.gahfy.devtools.customlink.DevToolsApplication;
import net.gahfy.devtools.customlink.data.injection.CustomScope;
import net.gahfy.devtools.customlink.data.manager.DatabaseService;

import dagger.Module;
import dagger.Provides;

/**
 * Module which provides database
 */
@Module  @CustomScope
public class DatabaseModule {
    /** The application in which the module is used */
    private DevToolsApplication mApplication;

    /**
     * Instantiates a new DatabaseModule.
     * @param application the application in which the module is used
     */
    public DatabaseModule(DevToolsApplication application){
        mApplication = application;
    }

    /**
     * Provides the database service in use in the application.
     * @return the database service in use in the application
     */
    @Provides @NonNull
    DatabaseService provideDatabaseService(){
        return DatabaseService.getInstance(mApplication);
    }
}
