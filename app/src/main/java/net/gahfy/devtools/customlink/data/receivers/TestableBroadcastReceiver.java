package net.gahfy.devtools.customlink.data.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class let unit tests check whether the receiver has been called or not
 */
public abstract class TestableBroadcastReceiver extends BroadcastReceiver {
    /**
     * Whether the BroadcastReceiver has been called since last reset
     */
    private static Map<String, Boolean> mCalled = new HashMap<>();

    public static boolean isCalled(String actionName) {
        return mCalled.containsKey(actionName) && mCalled.get(actionName) != null && mCalled.get(actionName);
    }

    public static void resetCalled(String actionName) {
        mCalled.put(actionName, false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mCalled.put(getActionName(), true);
    }

    public abstract String getActionName();
}
