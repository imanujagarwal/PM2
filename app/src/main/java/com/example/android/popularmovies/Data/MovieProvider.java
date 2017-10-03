package com.example.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.android.popularmovies.Data.MovieContract;
/**
 * Created by anuj on 31/8/17.
 */

public class MovieProvider extends ContentProvider {

    public MovieDbHelper db;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    @Override
    public boolean onCreate() {
        db = new MovieDbHelper(getContext());
        return true;
    }

    public static final int FAV_MOVIE_LIST = 101;

    public static final int FAV_MOVIE_LIST_ITEM = 102;


    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES, FAV_MOVIE_LIST);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES + "/#",FAV_MOVIE_LIST_ITEM);

        return matcher;

    }





    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retcursor;

        final SQLiteDatabase Dbhelper = db.getReadableDatabase();

        int mathc = sUriMatcher.match(uri);

        switch (mathc){
            case FAV_MOVIE_LIST:

                retcursor = Dbhelper.query(MovieContract.FavoriteMovies.TABLE_NAME, projection,selection,selectionArgs,null,null,sortOrder );
                break;

            default:
                throw new IllegalArgumentException("Cannot query URI" + uri);
        }

        return retcursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int select = sUriMatcher.match(uri);

        switch (select){
            case FAV_MOVIE_LIST:
                return MovieContract.FavoriteMovies.CONTENT_FAVORITES_TYPE;

            default:
                Toast.makeText(getContext(),"Error", Toast.LENGTH_LONG).show();
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase OpenHelper = db.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case FAV_MOVIE_LIST: {

                long _id = OpenHelper.insert(MovieContract.FavoriteMovies.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteMovies.buildFavoriteMoviesUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereclause, @Nullable String[] whereArgs) {
        SQLiteDatabase Dbhelper = db.getWritableDatabase();
        int wasSuccessful = -1;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAV_MOVIE_LIST:
                wasSuccessful = Dbhelper.delete(MovieContract.FavoriteMovies.TABLE_NAME, whereclause, whereArgs);
                break;
        }
        if (wasSuccessful > -1) {
            Toast.makeText(getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
        }
        return wasSuccessful;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
