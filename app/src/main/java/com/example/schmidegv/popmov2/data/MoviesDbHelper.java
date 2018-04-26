package com.example.schmidegv.popmov2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                        MoviesContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        MoviesContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                        MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL " +
                        ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
