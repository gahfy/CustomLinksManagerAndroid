package net.gahfy.devtools.customlink;


import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import net.gahfy.devtools.customlink.data.manager.DatabaseOpenHelper;
import net.gahfy.devtools.customlink.util.ArrayUtils;

import java.util.ArrayList;

public class DevToolsApplication extends Application{
    public SQLiteDatabase mDatabase;
    public int[] notificationList = new int[0];
    public ArrayList<NotificationUpdateListener> notificationUpdateListeners = new ArrayList<>();

    public SQLiteDatabase getDatabase(){
        if(mDatabase == null){
            DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            mDatabase = databaseOpenHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public int getNotificationIdIndex(int notificationId){
        for(int i=0; i<notificationList.length; i++){
            if(notificationId == notificationList[i])
                return i;
        }
        return -1;
    }

    public void removeNotification(int notificationId){
        int notificationIndex = getNotificationIdIndex(notificationId);
        if(notificationIndex != -1) {
            notificationList = ArrayUtils.removeIndex(notificationList, notificationIndex);
            for(NotificationUpdateListener notificationUpdateListener : notificationUpdateListeners)
                notificationUpdateListener.onNotificationRemoved(notificationId);
        }
    }

    public void addNotification(int notificationId){
        if(getNotificationIdIndex(notificationId) == -1) {
            notificationList = ArrayUtils.add(notificationList, notificationId);
            for(NotificationUpdateListener notificationUpdateListener : notificationUpdateListeners)
                notificationUpdateListener.onNotificationAdded(notificationId);
        }
    }

    public void addNotificationUpdateListener(NotificationUpdateListener notificationUpdateListener){
        notificationUpdateListeners.add(notificationUpdateListener);
    }

    public void removeNotificationUpdateListener(NotificationUpdateListener notificationUpdateListener){
        notificationUpdateListeners.remove(notificationUpdateListener);
    }

    public interface NotificationUpdateListener{
        void onNotificationAdded(int notificationId);
        void onNotificationRemoved(int notificationId);
    }
}
