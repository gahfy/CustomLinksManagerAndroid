package net.gahfy.devtools.customlink;


import org.junit.Assert;
import org.junit.Test;

public class DevToolsApplicationTest {
    private int counterTest = 0;

    @Test
    public void add_notification_listener() throws Exception {
        DevToolsApplication.NotificationUpdateListener notificationUpdateListener = new DevToolsApplication.NotificationUpdateListener() {
            @Override
            public void onNotificationAdded(int notificationId) {
                counterTest++;
            }

            @Override
            public void onNotificationRemoved(int notificationId) {
                counterTest--;
            }
        };

        DevToolsApplication devToolsApplication = new DevToolsApplication();
        devToolsApplication.addNotificationUpdateListener(notificationUpdateListener);
        devToolsApplication.addNotification(1);
        devToolsApplication.addNotification(2);
        devToolsApplication.addNotification(3);
        devToolsApplication.removeNotification(2);
        devToolsApplication.removeNotification(4);
        devToolsApplication.removeNotificationUpdateListener(notificationUpdateListener);
        Assert.assertEquals(counterTest, 2);
        devToolsApplication.removeNotification(3);
        Assert.assertEquals(counterTest, 2);
    }
}
