package com.example.lcout.mymovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by lcout on 20/03/2018.
 */

public class FavouriteContentProvider extends ContentProvider {

    public static final int FAVOURITE_MOVIES = 100;
    public static final UriMatcher sUriMatcher = buildUrlMatcher();

    public static UriMatcher buildUrlMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouriteContract.AUTHORITY, FavouriteContract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIES);

        return uriMatcher;
    }

    private FavoutireDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Context mContext = getContext();
        mDbHelper = new FavoutireDbHelper(mContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVOURITE_MOVIES:
                Long id = db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(FavouriteContract.FavouriteEntry.CONTENT_URI, id);
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //TODO: not sure if we need to notify changes.
        //getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
