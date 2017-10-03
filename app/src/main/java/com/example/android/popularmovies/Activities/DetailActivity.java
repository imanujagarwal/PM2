package com.example.android.popularmovies.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;
import com.example.android.popularmovies.AsyncTask.FetchData;

import static android.view.View.GONE;
import static java.lang.System.load;


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
    private boolean liked = false;

    String BASE_URL = "http://image.tmdb.org/t/p/w500//";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);


        // Enabling Up / Back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        final String MovieName = i.getStringExtra(MOVIE_NAME);
        final String MovieSynopsis = i.getStringExtra(MOVIE_SYNOPSIS);
        final double MovieRating = i.getDoubleExtra(MOVIE_RATING,0);
        final String MovieReleaseDate = i.getStringExtra(MOVIE_RELEASE_DATE);
        final String MovieId = i.getStringExtra(MOVIE_ID);
        final String ImageSuffix = i.getStringExtra(MOVIE_IMAGE);
        final String ImageURL = BASE_URL+ImageSuffix;
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
                        Toast.makeText(getApplicationContext(), "INSERTION DONE SUCCESFULY",
                                Toast.LENGTH_SHORT).show();
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



    public class TrailerResult extends FetchData {

        public final String BASE_YOUTUBE_URL = "https://m.youtube.com/watch?v=";


        public String getTrailerImage(String youtubeId, int image_number){
            return "https://img.youtube.com/vi/" + youtubeId + "/" + image_number +".jpg" ;
        }


        @Override
        protected void onPostExecute(final String[] result) {

            if(result == null)
                return;

            boolean isYoutubeInstalled = isPackageInstalled("com.google.android.youtube");

            ImageView Trailer1 = (ImageView) findViewById(R.id.trailer1);




            if (result.length >0) {

                Picasso.with(getApplicationContext())
                        .load(getTrailerImage(result[0],2))
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .into(Trailer1);

                if(isYoutubeInstalled){
                    Trailer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(BASE_YOUTUBE_URL + result[0]));
                            intent.setPackage("com.google.android.youtube");
                            startActivity(intent);
                        }
                    });
                }else {
                    Trailer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getBaseContext(),WebViewActivity.class);
                            intent.putExtra("youtubeUrl",BASE_YOUTUBE_URL + result[0]);
                            startActivity(intent);
                        }
                    });

                }

            } else{
                findViewById(R.id.trailer_card).setVisibility(GONE);
                findViewById(R.id.trailer_headline).setVisibility(GONE);
                //                Trailer1.setText("No trailers available for the movie");
            }

            ImageView Trailer2 = (ImageView) findViewById(R.id.trailer2);


            if (result.length >= 2) {
                Picasso.with(getApplicationContext())
                        .load(getTrailerImage(result[1],2))
                        .fit()
                        .placeholder(R.drawable.placeholder)
                        .into(Trailer2);

                if(isYoutubeInstalled){
                    Trailer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(BASE_YOUTUBE_URL + result[1]));
                            intent.setPackage("com.google.android.youtube");
                            startActivity(intent);
                        }
                    });
                } else {
                    Trailer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getBaseContext(),WebViewActivity.class);
                            intent.putExtra("youtubeUrl",BASE_YOUTUBE_URL + result[1]);
                            startActivity(intent);
                        }
                    });
                }

            } else{
                Trailer2.setVisibility(GONE);
            }
        }
    }

    public class ReviewResult extends FetchData{

        @Override
        protected void onPostExecute(final String[] result) {

            LinearLayout ll = (LinearLayout)findViewById(R.id.reviews);

            if(result == null || result.length == 0){
                TextView tv = new TextView(getApplicationContext());
                tv.setText("No reviews for this movie available");
                tv.setTextSize(15);
                tv.setTextColor(getResources().getColor(R.color.grey));
                ll.addView(tv);
                return;
            }

            for (int i=0;i<result.length;i++){
                CardView card = new CardView(getBaseContext());

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(0,16,0,16);

                card.setLayoutParams(params);

                // Set CardView corner radius
                card.setRadius(16);

                // Set cardView content padding
                card.setContentPadding(15, 15, 15, 15);

                // Set a background color for CardView
                card.setCardBackgroundColor(Color.parseColor("#FFA767B7"));

                // Set the CardView maximum elevation
                card.setMaxCardElevation(15);

                // Set CardView elevation
                card.setCardElevation(9);

                TextView tv = new TextView(getBaseContext());
                tv.setPadding(16,16,16,16);
                tv.setText(result[i]);
                tv.setTextSize(15);
                tv.setTextColor(getResources().getColor(R.color.cardview_light_background));

                card.addView(tv);

                ll.addView(card);
        }
    }

    }

    private boolean isPackageInstalled(String packagename) {
        try {
//            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            this.getPackageManager().getPackageInfo(packagename,0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
