package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by anuj on 10/3/17.
 */

public class DetailActivity extends AppCompatActivity {


    private String MOVIE_NAME = "title";
    private String MOVIE_IMAGE = "poster_path";
    private String MOVIE_SYNOPSIS = "overview";
    private String MOVIE_RATING = "vote_average";
    private String MOVIE_RELEASE_DATE = "release_date";
    String BASE_URL = "http://image.tmdb.org/t/p/w500//";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);


        // Enabling Up / Back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String MovieName = i.getStringExtra(MOVIE_NAME);
        String MovieSynopsis = i.getStringExtra(MOVIE_SYNOPSIS);
        double MovieRating = i.getDoubleExtra(MOVIE_RATING,0);
        String MovieReleaseDate = i.getStringExtra(MOVIE_RELEASE_DATE);
        String MovieId = i.getStringExtra("id");
        String ImageSuffix = i.getStringExtra(MOVIE_IMAGE);
        String ImageURL = BASE_URL+ImageSuffix;

        getSupportActionBar().setTitle(MovieName);

        new FetchVideoData().execute(MovieId);



        ImageView  imageView_artwork = (ImageView) findViewById(R.id.imageView_movieArtwork_detail);

        TextView tvMovieName = (TextView) findViewById(R.id.textView_moviename);
        tvMovieName.setText(MovieName);

        TextView tvRating = (TextView) findViewById(R.id.textview_rating);
        tvRating.setText(MovieRating+"");

        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        tvReleaseDate.setText(MovieReleaseDate);

        Picasso.with(this).load(ImageURL).into(imageView_artwork);
        TextView textView_summary = (TextView)findViewById(R.id.textview_description);
        textView_summary.setText(MovieSynopsis);

    }


    public class FetchVideoData extends AsyncTask<String, Void, String[]> {

        private String BASE_URL = "https://api.themoviedb.org/3/movie";

        private String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

        private String[] keys;
        private String[] getVideoInfoFromJson(String jsonString) throws JSONException {

            JSONObject moviesJson = new JSONObject(jsonString);
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            String[] resultsStr = new String[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                String key;

                JSONObject movie = moviesArray.getJSONObject(i);
                key = movie.getString("key");

                resultsStr[i] = key;

            }

            return resultsStr;
        }



        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieVideoString = null;

            try {
                Uri buildUri = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendPath(params[0])
                        .appendPath("videos")
                        .appendQueryParameter("api_key", getResources().getString(R.string.api_key))
                        .build();

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                URL url_video = new URL(buildUri.toString());

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    movieVideoString = null;
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    movieVideoString = null;
                    return null;
                }
                movieVideoString = stringBuffer.toString();


            } catch (Exception e) {
                movieVideoString = null;
                return null;
            } finally {
                if (urlConnection != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        return null;
                    } catch (final Exception e) {
                        return null;
                    }
                }
            }

            try {
                String[] s = getVideoInfoFromJson(movieVideoString);
                return s;
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String[] result) {

            TextView Trailer1 = (TextView) findViewById(R.id.trailer1);
            if (result.length >0) {
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
            } else{
                Trailer1.setText("No trailers available for the movie");
            }

            TextView Trailer2 = (TextView) findViewById(R.id.trailer2);
            if (result.length >= 2) {
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
            } else{
                Trailer2.setVisibility(View.GONE);
            }
        }
    }
}
