package net.gahfy.devtools.customlink.ui.custom_link;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;

import net.gahfy.devtools.customlink.DevToolsApplication;
import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.manager.DatabaseService;
import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.data.receivers.CustomLinkReceiver;
import net.gahfy.devtools.customlink.data.receivers.NotificationCancelReceiver;
import net.gahfy.devtools.customlink.ui.base.BasePresenter;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

public class CustomLinkListPresenter extends BasePresenter<CustomLinkListView> {
    private CustomLinkListAdapter mAdapter;

    private DevToolsApplication.NotificationUpdateListener mNotificationUpdateListener;

    @Override @SuppressWarnings("ConstantConditions")
    public void bind(CustomLinkListView view) throws NullPointerException{
        super.bind(view);
        mAdapter = new CustomLinkListAdapter(this);
        mNotificationUpdateListener = new DevToolsApplication.NotificationUpdateListener() {
            @Override
            public void onNotificationAdded(int notificationId) {
                for(int i=0; i<mAdapter.getItemCount(); i++){
                    if((int) mAdapter.getItemId(i) == notificationId - CustomLink.NOTIFICATION_ID_PREFIX){
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }

            @Override
            public void onNotificationRemoved(int notificationId) {
                for(int i=0; i<mAdapter.getItemCount(); i++){
                    if((int) mAdapter.getItemId(i) == notificationId - CustomLink.NOTIFICATION_ID_PREFIX){
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }
        };
        getView().getDevToolsApplication().addNotificationUpdateListener(mNotificationUpdateListener);
        getView().setAdapter(mAdapter);

        subscribeCursor(new DatabaseService.CursorSubscriber() {
            @Override
            public Cursor onDatabase(SQLiteDatabase database) {
                return CustomLink.findAll(database);
            }

            @Override
            public void onError(Throwable error) {
            }

            @Override
            public void onCursor(Cursor cursor, int cursorSize) throws NullPointerException{
                mAdapter.setData(cursor, cursorSize);
                mAdapter.notifyDataSetChanged();
                getView().setEmptyStateVisibility(cursorSize == 0);
            }
        });

        onResume();
    }

    @SuppressWarnings("ConstantConditions")
    void onResume() throws NullPointerException{
        getView().setTitle(getContext().getString(R.string.list_title));
    }

    @SuppressWarnings("ConstantConditions")
    boolean isCustomLinkNotified(int customLinkIdAsInt) throws NullPointerException {
        return getView().getDevToolsApplication().getNotificationIdIndex(customLinkIdAsInt + CustomLink.NOTIFICATION_ID_PREFIX) != -1;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void unbind() throws NullPointerException {
        getView().getDevToolsApplication().removeNotificationUpdateListener(mNotificationUpdateListener);
        super.unbind();
        mAdapter.unbind();
        mNotificationUpdateListener = null;
        mAdapter = null;
    }

    @SuppressWarnings("ConstantConditions")
    void onAddButtonClick() throws NullPointerException {
        getView().getContainerActivity().addUpdateFragment(null, -1, this);
    }

    @Override
    protected void inject() throws NullPointerException {
        getComponent().inject(this);
    }

    @SuppressWarnings("ConstantConditions")
    void onItemClick(CustomLink customLink) throws NullPointerException {
        try {
            getView().startActivity(customLink.getIntent());
        }
        catch(Exception e){
            ErrorViewUtils.showExceptionToast(getContext(), e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    void update(CustomLink customLink) throws NullPointerException{
        getView().getContainerActivity().addUpdateFragment(customLink, mAdapter.getPosition(customLink), this);
    }

    void notifyItemChanged(int position, Cursor cursor, int cursorSize){
        mAdapter.setData(cursor, cursorSize);
        mAdapter.notifyItemChanged(position);
    }

    @SuppressWarnings("ConstantConditions")
    void notifyItemInserted(Cursor cursor, int cursorSize) throws NullPointerException{
        mAdapter.setData(cursor, cursorSize);
        mAdapter.notifyItemInserted(0);
        getView().setEmptyStateVisibility(false);
    }

    void deleteCustomLink(final CustomLink customLink){
        subscribeCursor(new DatabaseService.CursorSubscriber() {
            @Override
            public Cursor onDatabase(SQLiteDatabase database) {
                customLink.deleteFromDb(database);
                return CustomLink.findAll(database);
            }

            @Override
            public void onError(Throwable error) {
            }

            @Override @SuppressWarnings("ConstantConditions")
            public void onCursor(Cursor cursor, int cursorSize) throws NullPointerException{
                int customLinkPosition = mAdapter.getPosition(customLink);
                mAdapter.setData(cursor, cursorSize);
                mAdapter.notifyItemRemoved(customLinkPosition);
                getView().setEmptyStateVisibility(cursorSize == 0);
                if(getView().getDevToolsApplication().getNotificationIdIndex(customLink.getNotificationId()) != -1) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(customLink.getNotificationId());
                    getView().getDevToolsApplication().removeNotification(customLink.getNotificationId());
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    void notifyCustomLink(CustomLink customLink) throws NullPointerException{
        if(NotificationManagerCompat.from(getContext()).areNotificationsEnabled()) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.notification_small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.notification_large_icon))
                    .setContentTitle(customLink.getCustomLinkTitle() == null ? getContext().getString(R.string.open_link) : customLink.getCustomLinkTitle())
                    .setContentText(customLink.getCustomLinkUri().toString())
                    .setOngoing(true);
            Intent resultIntent = new Intent(CustomLinkReceiver.CUSTOM_LINK_ACTION);
            resultIntent.putExtra(CustomLinkReceiver.CUSTOM_LINK_EXTRA, customLink);
            PendingIntent resultPendingIntent = PendingIntent.getBroadcast(getContext(), CustomLinkReceiver.CUSTOM_LINK_PENDING_INTENT_REQUEST_PREFIX + customLink.getIdAsInt(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent cancelIntent = new Intent(NotificationCancelReceiver.NOTIFICATION_CANCEL_ACTION);
            cancelIntent.putExtra(NotificationCancelReceiver.NOTIFICATION_CANCEL_EXTRA, customLink.getNotificationId());
            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getContext(), NotificationCancelReceiver.NOTIFICATION_CANCEL_PENDING_INTENT_REQUEST_PREFIX + customLink.getIdAsInt(), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_remove_circle_outline_black_24dp, getContext().getString(R.string.cancel), cancelPendingIntent).build());
            mNotificationManager.notify(customLink.getNotificationId(), mBuilder.build());

            getView().getDevToolsApplication().addNotification(customLink.getNotificationId());
        }
        else{
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContainerActivity());
            builder.setTitle(R.string.disabled_notification_dialog_title)
                    .setMessage(R.string.disabled_notification_dialog_message)
                    .setPositiveButton(R.string.disabled_notification_dialog_redirect, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", getView().getContainerActivity().getPackageName());
                            intent.putExtra("app_uid", getView().getContainerActivity().getApplicationInfo().uid);
                            getView().getContainerActivity().startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        }
    }

    @SuppressWarnings("ConstantConditions")
    void cancelNotification(CustomLink customLink) throws NullPointerException {
        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(customLink.getNotificationId());
        getView().getDevToolsApplication().removeNotification(customLink.getNotificationId());
    }
}
