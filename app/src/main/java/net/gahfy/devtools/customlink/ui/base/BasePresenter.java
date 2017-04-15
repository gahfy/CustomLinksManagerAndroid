package net.gahfy.devtools.customlink.ui.base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.component.DaggerPresenterComponent;
import net.gahfy.devtools.customlink.data.component.PresenterComponent;
import net.gahfy.devtools.customlink.data.manager.DatabaseService;
import net.gahfy.devtools.customlink.data.module.DatabaseModule;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.inject.Inject;

public abstract class BasePresenter<V extends BaseView> {
    private WeakReference<V> mView;

    @Inject
    DatabaseService mDatabaseService;

    private ArrayList<DatabaseService.CursorSubscriber> mCursorSubscribers;

    public void bind(V view) throws NullPointerException{
        this.mView = new WeakReference<>(view);
        mCursorSubscribers = new ArrayList<>();
        inject();
    }

    protected void subscribeCursor(DatabaseService.CursorSubscriber cursorSubscriber){
        DatabaseService.CursorSubscriber currentCursorSubscriber = new DatabaseService.CursorSubscriber(cursorSubscriber) {
            @Override
            public Cursor onDatabase(SQLiteDatabase database) {
                return getParent().onDatabase(database);
            }

            @Override
            public void onCursor(Cursor cursor, int cursorSize) {
                getParent().onCursor(cursor, cursorSize);
                mCursorSubscribers.remove(this);
            }

            @Override
            public void onError(Throwable error) {
                getParent().onError(error);
                if(getContext() != null) {
                    ErrorViewUtils.showErrorToast(getContext(), R.string.technical_error, error);
                }
                mCursorSubscribers.remove(this);
            }
        };
        mCursorSubscribers.add(currentCursorSubscriber);
        mDatabaseService.subscribeCursor(currentCursorSubscriber);
    }

    public void unbind(){
        while(mCursorSubscribers.size() > 0){
            DatabaseService.CursorSubscriber cursorSubscriber = mCursorSubscribers.get(0);
            mDatabaseService.dispose(cursorSubscriber);
            mCursorSubscribers.remove(cursorSubscriber);
        }
        this.mView = null;
    }

    protected abstract void inject();

    protected PresenterComponent getComponent() throws NullPointerException{
        if(getView() != null) {
            return DaggerPresenterComponent.builder()
                    .databaseModule(new DatabaseModule(getView().getDevToolsApplication()))
                    .build();
        }
        else{
            throw new NullPointerException("No view bound to the presenter");
        }
    }

    @Nullable
    protected V getView(){
        return mView == null ? null : mView.get();
    }

    @Nullable
    protected Context getContext(){
        if(getView() != null)
            return getView().getDevToolsApplication();
        return null;
    }
}
