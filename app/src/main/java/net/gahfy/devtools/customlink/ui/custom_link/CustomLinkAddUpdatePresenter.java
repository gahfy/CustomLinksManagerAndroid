package net.gahfy.devtools.customlink.ui.custom_link;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.manager.DatabaseService;
import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.ui.base.BasePresenter;

public class CustomLinkAddUpdatePresenter extends BasePresenter<CustomLinkAddUpdateView> {
    @Nullable
    private CustomLink mCustomLink;
    private CustomLinkListPresenter mParentPresenter;

    private int mPosition;
    private boolean mErrorHasBeenShown;

    @Override @SuppressWarnings("ConstantConditions")
    public void bind(CustomLinkAddUpdateView customLinkAddUpdateView) throws NullPointerException{
        super.bind(customLinkAddUpdateView);
        mErrorHasBeenShown = false;
        mCustomLink = getView().getCustomLink();
        mParentPresenter = getView().getParentPresenter();
        mPosition = getView().getPosition();
        getView().getContainerActivity().setMenuDoneVisible(true);
        getView().getContainerActivity().setMenuItemClickListener(new CustomLinkAddUpdateMenuItemClickListener(getContext(), this));
        initForm();
        if(mCustomLink != null){
            if(mCustomLink.getCustomLinkTitle() != null){
                getView().setTitle(mCustomLink.getCustomLinkTitle());
            }
            else {
                getView().setTitle(getContext().getString(R.string.edit_title));
            }
        }
        else {
            getView().setTitle(getContext().getString(R.string.add_title));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initForm() throws NullPointerException{
        if(mCustomLink != null) {
            getView().setForm(mCustomLink.getCustomLinkTitle(), mCustomLink.getCustomLinkUri().toString());
        }
        else{
            getView().setForm("", "");
        }
    }

    @Override @SuppressWarnings("ConstantConditions")
    public void unbind() throws NullPointerException{
        if(getView() != null) {
            getView().getContainerActivity().setMenuDoneVisible(false);
            getView().getContainerActivity().setMenuItemClickListener(null);
        }
        if(mParentPresenter != null) {
            mParentPresenter.onResume();
        }
        mCustomLink = null;
        mParentPresenter = null;
        super.unbind();
    }

    @SuppressWarnings("ConstantConditions")
    void submitForm() throws NullPointerException{
        if(!getView().getCustomLinkUri().trim().equals("")){
            persist();
        }
        else{
            mErrorHasBeenShown = true;
            getView().setError(getContext().getString(R.string.empty_uri_error));
        }
    }

    @SuppressWarnings("ConstantConditions")
    void onUriTextChanged(String currentText) throws NullPointerException{
        if(currentText.trim().equals("") && mErrorHasBeenShown){
            getView().setError(getContext().getString(R.string.empty_uri_error));
        }
        else{
            getView().setError("");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void persist() throws NullPointerException{
        if(mCustomLink != null) {
            mCustomLink.setCustomLinkTitle(getView().getCustomLinkTitle().trim().equals("") ? null : getView().getCustomLinkTitle().trim());
            mCustomLink.setCustomLinkUri(getView().getCustomLinkUri());
            subscribeCursor(new DatabaseService.CursorSubscriber() {
                @Override
                public Cursor onDatabase(SQLiteDatabase database) {
                    mCustomLink.updateInDb(database);
                    return CustomLink.findAll(database);
                }

                @Override
                public void onError(Throwable error) {
                }

                @Override
                public void onCursor(Cursor cursor, int cursorSize) throws NullPointerException {
                    mParentPresenter.notifyItemChanged(mPosition, cursor, cursorSize);
                    getView().getContainerActivity().getSupportFragmentManager().popBackStack();
                    if(getView().getDevToolsApplication().getNotificationIdIndex(mCustomLink.getNotificationId()) != -1){
                        NotificationManager mNotificationManager =
                                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancel(mCustomLink.getNotificationId());
                        mParentPresenter.notifyCustomLink(mCustomLink);
                    }
                }
            });
        }
        else{
            mCustomLink = new CustomLink(getView().getCustomLinkTitle().trim().equals("") ? null : getView().getCustomLinkTitle().trim(), getView().getCustomLinkUri());
            subscribeCursor(new DatabaseService.CursorSubscriber() {
                @Override
                public Cursor onDatabase(SQLiteDatabase database) {
                    mCustomLink.insertInDb(database);
                    return CustomLink.findAll(database);
                }

                @Override
                public void onError(Throwable error) {
                }

                @Override
                public void onCursor(Cursor cursor, int cursorSize) {
                    mParentPresenter.notifyItemInserted(cursor, cursorSize);
                    getView().getContainerActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    @Override
    protected void inject() throws NullPointerException{
        getComponent().inject(this);
    }
}
