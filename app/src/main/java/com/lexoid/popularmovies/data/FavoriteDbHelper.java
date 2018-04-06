package com.lexoid.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.MOVIE_ID_COLUMN;
import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.ORIGINAL_TITLE;
import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.OVERVIEW;
import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.POSTER_PATH;
import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.RELEASE_DATE;
import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.TABLE_NAME;
import static com.lexoid.popularmovies.data.FavoritesContract.MovieEntry.VOTE_AVERAGE;

public class FavoriteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;


    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" +
                MOVIE_ID_COLUMN + " INTEGER, " +
                ORIGINAL_TITLE  + " TEXT, " +
                POSTER_PATH  + " TEXT, " +
                OVERVIEW  + " TEXT, " +
                VOTE_AVERAGE  + " REAL, " +
                RELEASE_DATE  + " TEXT" +
                " );";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
