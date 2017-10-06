package com.example.android.popularmovies.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapters.ReviewAdapter;
import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Adapters.TrailerAdapter;
import com.squareup.picasso.Picasso;
import com.example.android.popularmovies.AsyncTask.FetchData;

import java.util.ArrayList;

import static android.view.View.GONE;


/**
 * Created by anuj on 10/3/17.
 */

public class DetailActivity extends AppCompatActivity {

    private String MOVIE_NAME = "title";
    private String MOVIE_IMAGE = "poster_path";
    private String MOVIE_SYNOPSIS = "overview";
    private String MOVIE_RATING = "vote_average";
    private String MOVIE_RELEASE_DATE = "release_date";
    private String MOVIE_ID = "id";
    private Context c;
    private boolean liked = false;
    RecyclerView reviewRecyclerView;
    private static final String RECYCLER_VIEW_POSITION_KEY = "rv_position_key";
    Parcelable savedRecyclerLayoutState;


    String BASE_URL = "http://image.tmdb.org/t/p/w500//";
//    @BindView(R.id.trailer_recyclerview)RecycrlerView trailerRecyclerView;
    ArrayList trailerUrl = new ArrayList();
    ArrayList<String> reviews = new ArrayList<String>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
//        ButterKnife.bind(this);

        if(savedInstanceState!=null){
            savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_VIEW_POSITION_KEY);
        }

        c= this;

        // Enabling Up / Back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        final String MovieName = i.getStringExtra(MOVIE_NAME);
        final String MovieSynopsis = i.getStringExtra(MOVIE_SYNOPSIS);
        final double MovieRating = i.getDoubleExtra(MOVIE_RATING,0);
        final String MovieReleaseDate = i.getStringExtra(MOVIE_RELEASE_DATE);
        final String MovieId = i.getStringExtra(MOVIE_ID);
        final String ImageSuffix = i.getStringExtra(MovieContract.FavoriteMovies.COLUMN_POSTER_PATH);
        final String backDropSuffix  = i.getStringExtra(MovieContract.FavoriteMovies.COLUMN_BACKDROP_PATH);
        final String ImageURL = BASE_URL+backDropSuffix;
        Log.i("TAG", "ImageURL: "+ImageURL);


        getSupportActionBar().setTitle(MovieName);

        ReviewResult rr = new ReviewResult();
        rr.execute(MovieId, "reviews");


        TrailerResult tr = new TrailerResult();
        tr.execute(MovieId, "videos");



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        String[] projectMoviePosterId = {MovieContract.FavoriteMovies.COLUMN_POSTER_PATH};

        String[] selectionArgs = {ImageSuffix};

        Cursor cursor = getContentResolver().query(MovieContract.FavoriteMovies.CONTENT_URI,
                projectMoviePosterId,
                MovieContract.FavoriteMovies.COLUMN_POSTER_PATH + "=?",
                selectionArgs,
                null);
        Log.i("TAG", "onCreate: CURSOR COUNT " +cursor.getCount());

        if(cursor.getCount() > 0){
            liked = true;
            fab.setImageResource(R.drawable.heart_filled);
        }




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(liked){
                    String[] whereArgs = {ImageSuffix};
                    getContentResolver().delete(MovieContract.FavoriteMovies.CONTENT_URI,MovieContract.FavoriteMovies.COLUMN_POSTER_PATH + "=?",whereArgs);
                    fab.setImageResource(R.drawable.heart_outline);
                    liked = false;


                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.FavoriteMovies.COLUMN_MOVIE_ID,MovieId);
                    contentValues.put(MovieContract.FavoriteMovies.COLUMN_BACKDROP_PATH, backDropSuffix);
                    contentValues.put(MOVIE_NAME,MovieName);
                    contentValues.put(MOVIE_SYNOPSIS,MovieSynopsis);
                    contentValues.put(MOVIE_RATING,MovieRating);
                    contentValues.put(MOVIE_RELEASE_DATE,MovieReleaseDate);
                    contentValues.put(MOVIE_IMAGE,ImageSuffix);

                    Uri uri = getContentResolver().insert(MovieContract.FavoriteMovies.CONTENT_URI, contentValues);

                    fab.setImageResource(R.drawable.heart_filled);
                    liked = true;
                    Toast.makeText(getApplicationContext(),"Movie added to Favorites",Toast.LENGTH_SHORT).show();

                    if (uri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(getApplicationContext(), "INSERTION COULD NOT BE DONE",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
//                        Toast.makeText(getApplicationContext(), "INSERTION DONE SUCCESFULY", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });








        ImageView  imageView_artwork = (ImageView) findViewById(R.id.imageView_movieArtwork_detail);

        TextView tvMovieName = (TextView) findViewById(R.id.textView_moviename);
        tvMovieName.setText(MovieName);

        TextView tvRating = (TextView) findViewById(R.id.textview_rating);
        tvRating.setText(MovieRating+"");

        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        tvReleaseDate.setText(MovieReleaseDate);

        Picasso.with(this)
                .load(ImageURL)
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(imageView_artwork);

        TextView textView_summary = (TextView)findViewById(R.id.textview_description);
        textView_summary.setText(MovieSynopsis);

    }
    int currentVisiblePosition = 0;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RECYCLER_VIEW_POSITION_KEY, reviewRecyclerView.getLayoutManager().onSaveInstanceState());
    }





    public class TrailerResult extends FetchData {

        @Override
        protected void onPostExecute(final String[] result) {


            for (int i =0; i<result.length ; i++){
                trailerUrl.add(result[i]);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.HORIZONTAL,false);
            TrailerAdapter trailerAdapter = new TrailerAdapter(trailerUrl,c);
            RecyclerView trailerRecyclerView = (RecyclerView) findViewById(R.id.trailer_recyclerview);
            trailerRecyclerView.setLayoutManager(linearLayoutManager);
            trailerRecyclerView.setAdapter(trailerAdapter);

            if(result == null)
                return;

            if(result.length < 1)
                findViewById(R.id.trailer_headline).setVisibility(GONE);

        }
    }

    public class ReviewResult extends FetchData{

        @Override
        protected void onPostExecute(final String[] result) {

            for (int i =0; i<result.length ; i++){
                reviews.add(result[i]);
            }
            if(reviews.size() < 1){
                findViewById(R.id.review_recyclerview).setVisibility(View.GONE);
                findViewById(R.id.review_headline).setVisibility(View.GONE);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false);
            ReviewAdapter reviewAdapter = new ReviewAdapter(reviews,c);
            reviewRecyclerView = (RecyclerView) findViewById(R.id.review_recyclerview);
            reviewRecyclerView.setNestedScrollingEnabled(false);
            reviewRecyclerView.setLayoutManager(linearLayoutManager);
            reviewRecyclerView.setAdapter(reviewAdapter);
            reviewRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);


        }

    }

}
