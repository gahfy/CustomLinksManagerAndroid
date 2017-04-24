package net.gahfy.devtools.customlink.data.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import net.gahfy.devtools.customlink.DevToolsApplication;

/**
 * Receiver for when a notification must be canceled
 */
public class NotificationCancelReceiver extends TestableBroadcastReceiver {
    /**
     * <p>The prefix for the Pending Intent.</p>
     */
    public static final int NOTIFICATION_CANCEL_PENDING_INTENT_REQUEST_PREFIX = 20000;

    /**
     * The action that will call this receiver
     */
    public static final String NOTIFICATION_CANCEL_ACTION =
            "net.gahfy.devtools.customlink.data.receivers.NotificationCancelReceiver";

    /**
     * The name of the notification id in the bundle of the intent
     */
    public static final String NOTIFICATION_CANCEL_EXTRA =
            "net.gahfy.devtools.customlink.data.receivers.NotificationCancelReceiver.notificationId";

    /**
     * Cancels the notification.
     * @param context Context in which the application is running.
     * @param intent Intent calling this receiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(intent.getIntExtra(NOTIFICATION_CANCEL_EXTRA, 0));
        ((DevToolsApplication) context.getApplicationContext()).removeNotification(intent.getIntExtra(NOTIFICATION_CANCEL_EXTRA, 0));
    }

    @Override
    public String getActionName() {
        return NOTIFICATION_CANCEL_ACTION;
    }
}
