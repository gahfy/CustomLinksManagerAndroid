package net.gahfy.devtools.customlink.data.model;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * <p>Class defining a Custom Link.</p>
 * <p>A Custom link is a link with a title, and a uri Android can open.</p>
 */
public class CustomLink implements Parcelable{
    /**
     * <p>The prefix for identifiers of notifications.</p>
     * <p>That means notification will have id PREFIX+CustomLinkId</p>
     */
    public static final int NOTIFICATION_ID_PREFIX = 10000;

    /** The name of the table containing the custom links in the database */
    private static final String TABLE_NAME = "custom_link";

    /** The name of the field containing the unique identifier of the custom link in the database */
    private static final String FIELD_NAME_ID = "custom_link_id";
    /** The name of the field containing the title of the custom link in the database */
    private static final String FIELD_NAME_TITLE = "custom_link_title";
    /** The name of the field containing the uri of the custom link in the database */
    private static final String FIELD_NAME_URI = "custom_link_uri";

    /**
     * <p>The unique identifier of the custom link.</p>
     * <p>It is -1 unless it has been persisted in database.</p>
     */
    private long id = -1;

    /** The title of the custom link */
    @Nullable
    private String customLinkTitle;

    /** The uri of the custom link */
    @NonNull
    private Uri customLinkUri;

    /** The creator to instantiates CustomLinks from Parcels */
    public static final Creator<CustomLink> CREATOR = new Creator<CustomLink>() {
        @Override
        public CustomLink createFromParcel(Parcel in) {
            return new CustomLink(in);
        }

        @Override
        public CustomLink[] newArray(int size) {
            return new CustomLink[size];
        }
    };

    /**
     * Instantiates a new CustomLink with values of data.
     * @param title the title of the custom link to set
     * @param customLinkUri the uri of the custom link to set
     */
    public CustomLink(@Nullable String title, @NonNull String customLinkUri){
        this.customLinkTitle = title;
        this.customLinkUri = Uri.parse(customLinkUri);
    }

    /**
     * Instantiates a new CustomLink from a Parcel.
     * @param in the parcel from which to instantiates the CustomLink
     */
    private CustomLink(Parcel in) {
        id = in.readLong();
        customLinkTitle = in.readString();
        customLinkUri = in.readParcelable(Uri.class.getClassLoader());
    }

    /**
     * Returns the unique identifier of the custom link.
     * @return the unique identifier of the custom link
     */
    public long getId(){
        return id;
    }

    /**
     * Returns the unique identifier of the custom link as an integer.
     * @return the unique identifier of the custom link as an integer
     */
    public int getIdAsInt(){
        return (int) id;
    }

    /**
     * Returns the identifier of the notification of the custom link.
     * @return the identifier of the notification of the custom link
     */
    public int getNotificationId(){
        return getIdAsInt() + NOTIFICATION_ID_PREFIX;
    }

    /**
     * Returns the uri of the custom link.
     * @return the uri of the custom link
     */
    @NonNull
    public Uri getCustomLinkUri(){
        return customLinkUri;
    }

    /**
     * Returns the title of the custom link.
     * @return the title of the custom link
     */
    @Nullable
    public String getCustomLinkTitle() { return customLinkTitle; }

    /**
     * Returns the intent to open the custom link.
     * @return the intent to open the custom link
     */
    @NonNull
    public Intent getIntent(){
        return new Intent(Intent.ACTION_VIEW, getCustomLinkUri());
    }

    /**
     * Sets the uri of the custom link.
     * @param customLinkUri the uri of the custom link to set
     */
    public void setCustomLinkUri(@NonNull String customLinkUri){
        this.customLinkUri = Uri.parse(customLinkUri);
    }

    /**
     * Sets the title of the custom link.
     * @param customLinkTitle the title of the custom link to set
     */
    public void setCustomLinkTitle(@Nullable String customLinkTitle){
        this.customLinkTitle = customLinkTitle;
    }

    @Override
    public String toString(){
        return customLinkUri.toString();
    }

    /**
     * Returns the Cursor with all the custom links ordered by id desc (so by date of creation
     * desc).
     * @param db the database from which to retrieve the custom links.
     * @return the Cursor with all the custom links ordered by id desc (so by date of creation desc)
     */
    public static Cursor findAll(SQLiteDatabase db){
        return db.query(TABLE_NAME, new String[]{FIELD_NAME_ID, FIELD_NAME_TITLE, FIELD_NAME_URI}, null, null, null, null, FIELD_NAME_ID.concat(" DESC"));
    }

    /**
     * Deletes this custom link in database.
     * @param db the database in which to delete the custom link.
     * @return the number of rows deleted (should always be 1)
     */
    public int deleteFromDb(SQLiteDatabase db){
        return db.delete(TABLE_NAME, FIELD_NAME_ID.concat(" = ?"), new String[]{String.valueOf(id)});
    }

    /**
     * Updates this custom link in database.
     * @param db the database in which to update the custom link.
     * @return the number of rows updated (should always be 1)
     */
    public int updateInDb(SQLiteDatabase db){
        return db.update(TABLE_NAME, getContentValues(), FIELD_NAME_ID.concat(" = ?"), new String[]{String.valueOf(id)});
    }

    /**
     * Inserts this custom link in database.
     * @param db the database in which to insert the custom link
     */
    public void insertInDb(SQLiteDatabase db){
        this.id = db.insert(TABLE_NAME, null, getContentValues());
    }

    /**
     * Returns the custom link described by the content of the current row of the specified cursor.
     * @param cursor the cursor positioned at the row from which to extract the content
     * @return the custom link described by the content of the current row of the specified cursor
     */
    public static CustomLink cursorPositionedToObject(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(FIELD_NAME_ID));
        String customLinkUri = cursor.getString(cursor.getColumnIndex(FIELD_NAME_URI));
        String customLinkTitle = cursor.getString(cursor.getColumnIndex(FIELD_NAME_TITLE));
        CustomLink result = new CustomLink(customLinkTitle, customLinkUri);
        result.id = id;
        return result;
    }

    /**
     * Returns the content values describing this custom link.
     * @return the content values describing this custom link
     */
    private ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME_TITLE, getCustomLinkTitle());
        contentValues.put(FIELD_NAME_URI, getCustomLinkUri().toString());
        return contentValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(customLinkTitle);
        dest.writeParcelable(customLinkUri, flags);
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof CustomLink){
            CustomLink customLink = (CustomLink) object;
            if(customLink.getId() == getId()){
                if(customLink.getCustomLinkUri().toString().equals(getCustomLinkUri().toString())){
                    if(customLink.getCustomLinkTitle() != null && getCustomLinkTitle() != null){
                        return customLink.getCustomLinkTitle().equals(getCustomLinkTitle());
                    }
                    return (customLink.getCustomLinkTitle() == null && getCustomLinkTitle() == null);
                }
            }
        }
        return false;
    }
}
