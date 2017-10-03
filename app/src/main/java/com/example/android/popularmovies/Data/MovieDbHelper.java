package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.Data.MovieContract.FavoriteMovies;

/**
 * Created by anuj on 30/8/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private Context context;
    private static final String DB_NAME = "Movies.db";

    MovieDbHelper(Context context ){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.FavoriteMovies.TABLE_NAME + " (" +
                MovieContract.FavoriteMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  // REQUIRED!!!

                MovieContract.FavoriteMovies.COLUMN_MOVIE_ID + " INTEGER  UNIQUE ON CONFLICT REPLACE, " +
                MovieContract.FavoriteMovies.COLUMN_POPULAR_INDEX + " INTEGER, " +
                MovieContract.FavoriteMovies.COLUMN_TOP_RATED_INDEX + " INTEGER, " +
                MovieContract.FavoriteMovies.COLUMN_OVERVIEW + " TEXT , " +
                MovieContract.FavoriteMovies.COLUMN_POSTER_PATH + " TEXT , " +
                MovieContract.FavoriteMovies.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_RUNTIME + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_TITLE + " TEXT , " +
                MovieContract.FavoriteMovies.COLUMN_VOTE_AVERAGE + " TEXT , " +
                MovieContract.FavoriteMovies.COLUMN_VOTE_COUNT + " TEXT , " +
                MovieContract.FavoriteMovies.COLUMN_TAGLINE + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_TRAILER + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_TRAILER2 + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_TRAILER3 + " TEXT, "  +
                MovieContract.FavoriteMovies.COLUMN_TRAILER_NAME + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_TRAILER2_NAME + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_TRAILER3_NAME + " TEXT, "  +
                MovieContract.FavoriteMovies.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                MovieContract.FavoriteMovies.COLUMN_FAVORITE_TIMESTAMP + " INTEGER ," +
                MovieContract.FavoriteMovies.COLUMN_DETAILS_LOADED  + " INTEGER DEFAULT 0, " +
                MovieContract.FavoriteMovies.COLUMN_REVIEW + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_REVIEW2 + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_REVIEW3 + " TEXT, "  +
                MovieContract.FavoriteMovies.COLUMN_REVIEW_NAME + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_REVIEW2_NAME + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_REVIEW3_NAME + " TEXT, "  +
                MovieContract.FavoriteMovies.COLUMN_SPARE_INTEGER  + " INTEGER DEFAULT 0, " +
                MovieContract.FavoriteMovies.COLUMN_SPARE_STRING1 + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_SPARE_STRING2 + " TEXT, " +
                MovieContract.FavoriteMovies.COLUMN_SPARE_STRING3 + " TEXT, "  +
                MovieContract.FavoriteMovies.COLUMN_MYNAME + " TEXT " +

                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovies.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
