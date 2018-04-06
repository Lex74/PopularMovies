package com.lexoid.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.TABLE_NAME;

public class FavoritesContentProvider extends ContentProvider {
    private FavoriteDbHelper favoriteDbHelper;

    private static UriMatcher uriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;
    public static final int ONE_MOVIE = 101;

    @Override
    public boolean onCreate() {
        favoriteDbHelper = new FavoriteDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)){
            case MOVIES:
                return favoriteDbHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            default:
                throw new UnsupportedOperationException(" Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = favoriteDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case MOVIES:
                long rowId = db.insert(TABLE_NAME, null, values);
                if (rowId > 0) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(" Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoriteDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        switch (match){
            case MOVIES:
                return db.delete(FavoritesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException(" Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_MOVIES+"/#", ONE_MOVIE);
        return uriMatcher;
    }
}
