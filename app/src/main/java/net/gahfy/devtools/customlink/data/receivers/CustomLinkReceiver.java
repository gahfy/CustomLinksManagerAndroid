package net.gahfy.devtools.customlink.data.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

/**
 * Receiver for when a custom link must be opened.
 */
public class CustomLinkReceiver extends BroadcastReceiver {
    /**
     * <p>The prefix of pending intent.</p>
     */
    public static final int CUSTOM_LINK_PENDING_INTENT_REQUEST_PREFIX = 10000;

    /**
     * The action that will call this receiver
     */
    public static final String CUSTOM_LINK_ACTION =
            "net.gahfy.devtools.customlink.data.receivers.CustomLinkReceiver";

    /**
     * The name of the custom link value in the bundle of the intent
     */
    public static final String CUSTOM_LINK_EXTRA =
            "net.gahfy.devtools.customlink.data.receivers.CustomLinkReceiver.customLink";

    /**
     * Open the Custom Link.
     * @param context Context in which the application is running
     * @param intent Intent calling this receiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            CustomLink customLink = intent.getExtras().getParcelable(CUSTOM_LINK_EXTRA);
            if(customLink != null) {
                Intent customLinkIntent = customLink.getIntent();
                customLinkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(customLinkIntent);
            }
        }
        catch(Exception e){
            ErrorViewUtils.showExceptionToast(context, e);
        }
    }
}
