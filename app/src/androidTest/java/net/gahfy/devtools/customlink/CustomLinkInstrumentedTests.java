package net.gahfy.devtools.customlink;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.view.View;
import android.widget.TextView;

import net.gahfy.devtools.customlink.data.receivers.CustomLinkReceiver;
import net.gahfy.devtools.customlink.data.receivers.NotificationCancelReceiver;
import net.gahfy.devtools.customlink.data.receivers.TestableBroadcastReceiver;
import net.gahfy.devtools.customlink.ui.activity.ContainerActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomLinkInstrumentedTests {
    @Rule
    public IntentsTestRule<ContainerActivity> mActivityRule = new IntentsTestRule<>(
            ContainerActivity.class);
    private String mCustomLinkTitle;
    private String mCustomLinkUri;
    private UiDevice mDevice;

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void initValidString() {
        mCustomLinkTitle = "Test Link";
        mCustomLinkUri = "test://uri";
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void addRemoveCustomLink() throws Exception {
        // Get title and uri of first link before any operation
        String firstCellTitle = getText(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lbl_custom_link_title));
        String firstCellUri = getText(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lbl_custom_link_uri));

        addCustomLink(mCustomLinkTitle, mCustomLinkUri);

        // Check that view has been added
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lbl_custom_link_title))
                .check(matches(withText(mCustomLinkTitle)));
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lbl_custom_link_uri))
                .check(matches(withText(mCustomLinkUri)));

        // Click on ongoing notification
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lt_menu_container))
                .perform(click());
        onView(withText(R.string.ongoing_notification)).inRoot(isPlatformPopup()).perform(click());


        // Check click on notification
        clickOnNotification(mCustomLinkUri);
        Thread.sleep(300);
        assertTrue(TestableBroadcastReceiver.isCalled(CustomLinkReceiver.CUSTOM_LINK_ACTION));
        TestableBroadcastReceiver.resetCalled(CustomLinkReceiver.CUSTOM_LINK_ACTION);
        // Check that intent with action view has been performed
        intended(allOf(hasAction("android.intent.action.VIEW")));

        // Click on Cancel on notification
        clickOnCancelNotification();
        Thread.sleep(300);
        assertTrue(TestableBroadcastReceiver.isCalled(NotificationCancelReceiver.NOTIFICATION_CANCEL_ACTION));
        TestableBroadcastReceiver.resetCalled(NotificationCancelReceiver.NOTIFICATION_CANCEL_ACTION);

        Thread.sleep(300);
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lt_menu_container))
                .perform(click());
        // Click on Ongoing notification
        onView(withText(R.string.ongoing_notification)).perform(click());

        // Wait for notification operations being performed
        Thread.sleep(300);

        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lt_menu_container))
                .perform(click());
        // Click on Cancel notification
        onView(withText(R.string.cancel_notification)).perform(click());


        // Remove the added Custom Link
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lt_menu_container))
                .perform(click());
        onView(withText(R.string.delete)).inRoot(isPlatformPopup()).perform(click());

        // Check that first view is the same as before add/remove
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lbl_custom_link_title))
                .check(matches(withText(firstCellTitle)));
        onView(withRecyclerView(R.id.rv_custom_link_list).atPositionOnView(0, R.id.lbl_custom_link_uri))
                .check(matches(withText(firstCellUri)));
    }

    public void addCustomLink(String customLinkTitle, String customLinkUri) {
        // Click on the button to add a custom link
        onView(withId(R.id.fab_add_custom_link))
                .perform(click());

        // Set custom link title and uri
        onView(withId(R.id.txt_custom_link_title))
                .perform(typeText(customLinkTitle), closeSoftKeyboard());
        onView(withId(R.id.txt_custom_link_uri))
                .perform(typeText(customLinkUri), closeSoftKeyboard());

        // Click on Submit
        onView(withId(R.id.menu_done))
                .perform(click());
    }

    public void clickOnNotification(String customLinkUri) {
        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.text(mCustomLinkTitle)), 10000);
        UiObject2 notificationTitle = mDevice.findObject(By.text(customLinkUri));
        notificationTitle.click();
    }

    public void clickOnCancelNotification() {
        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.text("CANCEL")), 10000);
        UiObject2 notificationCancel = mDevice.findObject(By.text("CANCEL"));
        notificationCancel.click();
    }

    private String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }
}
