package net.gahfy.devtools.customlink.data.manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gahfy.devtools.customlink.DevToolsApplication;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <p>This class allows to perform SQL request on a separate thread in order not to block the UI</p>
 * <p>It works with a stack of <code>CursorSubscriber</code> which are executed in order of provided</p>
 */
public class DatabaseService {
    /** Instance of database service, to be provided with Singleton */
    private static DatabaseService databaseServiceInstance;
    /** The application in which the DatabaseService is used */
    private DevToolsApplication mDevToolsApplication;
    /** The database bound to the service */
    private SQLiteDatabase mDatabase;
    /** The list of cursor subscriber attached to the service */
    private ArrayList<CursorSubscriber> mCursorSubscribers;
    /** The disposable of the current "get database" observable */
    private Disposable mDatabaseDisposable;
    /** The disposable of the current "get cursor" observable */
    private Disposable mCursorDisposable = null;
    /** Whether the current "get database" task is complete or not */
    private boolean mDatabaseTaskComplete = true;
    /** Whether the current "get cursor" task is complete or not */
    private boolean mCursorTaskComplete = true;
    /** The index of the cursor subscriber of the current operation*/
    private int mCurrentCursorSubscriberIndex;

    /**
     * Instantiates a new DatabaseService.
     * @param devToolsApplication the application in which the DatabaseService is used.
     */
    private DatabaseService(DevToolsApplication devToolsApplication){
        mDevToolsApplication = devToolsApplication;
        mCursorSubscribers = new ArrayList<>();
        mCurrentCursorSubscriberIndex = -1;
        mDatabase = null;
    }

    /**
     * Returns an instance of DatabaseService.
     * @param devToolsApplication the application in which the DatabaseService is used
     * @return an instance of DatabaseService
     */
    public static DatabaseService getInstance(DevToolsApplication devToolsApplication){
        if(databaseServiceInstance == null){
            databaseServiceInstance = new DatabaseService(devToolsApplication);
        }
        return databaseServiceInstance;
    }

    /**
     * Subscribes to a new Cursor operation.
     * @param cursorSubscriber the cursor subscriber to subscribe to the Cursor operation
     */
    public void subscribeCursor(@NonNull CursorSubscriber cursorSubscriber){
        mCursorSubscribers.add(cursorSubscriber);
        mCurrentCursorSubscriberIndex++;
        performOperations();
    }

    /**
     * Performs the operations (get database and get cursor) of the current CursorSubscriber
     */
    private void performOperations(){
        if(mDatabaseTaskComplete && mCursorTaskComplete && getCurrentCursorSubscriber() != null) {
            mDatabaseTaskComplete = false;
            getDatabaseObservable().subscribe(getDatabaseObserver());
        }
    }

    /**
     * <p>Returns a database observer for performing the operations.</p>
     * <p>When the database is available, we perform the operation with a ResultObserver</p>
     * @return the database observer for performing the operations
     */
    private Observer<SQLiteDatabase> getDatabaseObserver(){
        return new Observer<SQLiteDatabase>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                mDatabaseDisposable = disposable;
            }

            @Override
            public void onNext(SQLiteDatabase database) {
                mDatabase = database;
                mCursorTaskComplete = false;
                // Performs the operations with the retrieved database
                getResultObservable().subscribe(getResultObserver());
            }

            @Override
            public void onError(Throwable e) {
                if (getCurrentCursorSubscriber() != null) {
                    getCurrentCursorSubscriber().onError(e);
                    mCursorSubscribers.set(mCurrentCursorSubscriberIndex, null);
                }
            }

            @Override
            public void onComplete() {
                mDatabaseTaskComplete = true;
                performOperations();
            }
        };
    }

    /**
     * Returns an observer on the result of a database operation.
     * @return the observer on the result of a database operation
     */
    private Observer<Result> getResultObserver(){
        return new Observer<Result>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                mCursorDisposable = disposable;
            }

            @Override
            public void onNext(Result result) {
                if (result.getCursor() != null && getCurrentCursorSubscriber() != null) {
                    try {
                        getCurrentCursorSubscriber().onCursor(result.getCursor(), result.getCursorSize());
                    }
                    catch(Exception e){
                        onError(e);
                    }
                }
                else
                    onError(new NullPointerException("Result cursor and current cursor subscriber must be not null"));
            }

            @Override
            public void onError(Throwable e) {
                if (getCurrentCursorSubscriber() != null)
                    getCurrentCursorSubscriber().onError(e);
            }

            @Override
            public void onComplete() {
                mCursorTaskComplete = true;
                if(getCurrentCursorSubscriber() != null)
                    mCursorSubscribers.set(mCurrentCursorSubscriberIndex, null);
                performOperations();
            }
        };
    }

    /**
     * Disposes the current operation.
     */
    public void dispose(CursorSubscriber cursorSubscriber){
        if(getCurrentCursorSubscriber() == cursorSubscriber) {
            if (mDatabaseDisposable != null && !mDatabaseTaskComplete) {
                mDatabaseDisposable.dispose();
                mDatabaseTaskComplete = true;
                performOperations();
            }
            if (mCursorDisposable != null && !mCursorTaskComplete) {
                mCursorDisposable.dispose();
                mCursorTaskComplete = true;
                performOperations();
            }
        }
        else if(mCursorSubscribers.indexOf(cursorSubscriber) != -1){
            mCursorSubscribers.remove(cursorSubscriber);
        }
    }

    /**
     * Returns the current CursorSubscriber.
     * @return the current CursorSubscriber
     * @see CursorSubscriber
     */
    @Nullable
    private CursorSubscriber getCurrentCursorSubscriber(){
        if(mCursorSubscribers.size() > 0)
            return mCursorSubscribers.get(mCurrentCursorSubscriberIndex);
        return null;
    }

    /**
     * Returns the observable that will provide the Result.
     * @return the observable that will provide the Result
     * @see Result
     */
    private Observable<Result> getResultObservable(){
        return Observable.defer(new Callable<ObservableSource<? extends Result>>() {
            @Override
            public ObservableSource<? extends Result> call() throws Exception {
                if(getCurrentCursorSubscriber() != null) {
                    Cursor cursor = getCurrentCursorSubscriber().onDatabase(mDatabase);
                    Result result = new Result(cursor, cursor.getCount());
                    return Observable.just(result);
                }
                return Observable.just(new Result(null, -1));
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Returns the observable that will provide the SQLiteDatabase.
     * @return the observable that will provide the SQLiteDatabase
     */
    private Observable<SQLiteDatabase> getDatabaseObservable(){
        if(mDatabase == null) {
            return Observable.defer(new Callable<ObservableSource<? extends SQLiteDatabase>>() {
                @Override
                public ObservableSource<? extends SQLiteDatabase> call() throws Exception {
                    return Observable.just(mDevToolsApplication.getDatabase());
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        else{
            return Observable.just(mDatabase);
        }
    }

    /**
     * Interface definition for callbacks to be invoked when database operation is performed.
     * The onDatabase method will be run in a separate thread, and should return the cursor.
     * When the cursor is available, the onCursor method will be called
     */
    public static abstract class CursorSubscriber{
        /**
         * The parent subscriber on which this CursorSubscriber depends.
         */
        private CursorSubscriber mParent;

        /**
         * Instantiates a new CursorSubscriber
         */
        protected CursorSubscriber(){
            super();
        }

        /**
         * Instantiates a new CursorSubscriber
         * @param parent the parent subscriber on which this CursorSubscriber depends to set
         */
        protected CursorSubscriber(CursorSubscriber parent){
            super();
            mParent = parent;
        }

        /**
         * Returns the parent subscriber on which this CursorSubscriber depends.
         * @return the parent subscriber on which this CursorSubscriber depends
         */
        protected CursorSubscriber getParent(){
            return mParent;
        }

        /**
         * Called when the database is available.
         * @param database The SQLiteDatabase instance
         * @return the cursor of operations made with database
         */
        public abstract Cursor onDatabase(SQLiteDatabase database);

        /**
         * Called when the Cursor is available.
         * @param cursor The Cursor with the results
         * @param cursorSize the size of the cursor
         */
        public abstract void onCursor(Cursor cursor, int cursorSize);

        /**
         * Called when an error occurs.
         * @param error the error that occurred
         */
        public abstract void onError(Throwable error);
    }

    /**
     * Inner class to store a Cursor result.
     */
    private static class Result{
        /**
         * The cursor of the result
         */
        private Cursor mCursor;

        /**
         * The size of the cursor of the result
         */
        private int mCursorSize;

        /**
         * Instantiates a new Result.
         * @param cursor the cursor of the result to set
         * @param cursorSize the size of the cursor of the result to set
         */
        private Result(Cursor cursor, int cursorSize){
            mCursor = cursor;
            mCursorSize = cursorSize;
        }

        /**
         * Returns the cursor of the result.
         * @return the cursor of the result
         */
        private Cursor getCursor() {
            return mCursor;
        }

        /**
         * Returns the size of the cursor of the result.
         * @return the size of the cursor of the result
         */
        private int getCursorSize(){
            return mCursorSize;
        }
    }
}
