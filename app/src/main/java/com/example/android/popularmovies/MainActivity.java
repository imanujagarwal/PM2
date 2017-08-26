package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.R.id.rv_movies;


public class MainActivity extends AppCompatActivity {


    private int page = 1;
    MovieAdapter movieAdapter;
    List<GridItem> gridItemList;
    RecyclerView recyclerview;
    String selectedType = "popular";

    private String KEY = "api_key"; //Do not change this value, key must be defined on strings.xml
    private String RESULTS = "results";
    private String MOVIE_NAME = "title";
    private String MOVIE_IMAGE = "poster_path";
    private String MOVIE_SYNOPSIS = "overview";
    private String MOVIE_RATING = "vote_average";
    private String MOVIE_RELEASE_DATE = "release_date";
    private String PAGE = "page";
    private String pageNumber = "";
    private String MOVIE_ID = "id";

    public static final String TAG = "MyActivity";
    public FetchMovieInfo task = null;

    private EndlessScrollListener scrollListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = (RecyclerView) findViewById(rv_movies);
        gridItemList = new ArrayList<GridItem>();


        movieAdapter = new MovieAdapter(this, gridItemList);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerview.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadnextdatafromApi(page);
            }
        };

        recyclerview.addOnScrollListener(scrollListener);

    }

    public void loadnextdatafromApi(int page){
        new FetchMovieInfo().execute(selectedType, page+"");

    }


    public class FetchMovieInfo extends AsyncTask<String, Void, String[]> {

        private String LOG_TAG = FetchMovieInfo.class.getSimpleName();
        private String BASE_URL = "https://api.themoviedb.org/3/movie";


        private String[] getMovieInfoFromJson(String movieJsonString) throws JSONException {

            JSONObject moviesJson = new JSONObject(movieJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            String[] resultsStr = new String[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                String name;
                String posterPath;
                String synopsis;
                double rating;
                String released;
                String movieId;

                JSONObject movie = moviesArray.getJSONObject(i);
                name = movie.getString(MOVIE_NAME);
                posterPath = movie.getString(MOVIE_IMAGE);
                synopsis = movie.getString(MOVIE_SYNOPSIS);
                rating = movie.getDouble(MOVIE_RATING);
                released = movie.getString(MOVIE_RELEASE_DATE);
                movieId = movie.getString(MOVIE_ID);

                gridItemList.add(new GridItem(posterPath, name, synopsis, rating, released, movieId));

            }

            return resultsStr;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonString = null;

            try {
                Uri buildUri = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(KEY, getResources().getString(R.string.api_key))
                        .appendQueryParameter(PAGE, params[1])
                        .build();

                Log.i(TAG, "doInBackground: "+buildUri.toString());

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                URL url_video = new URL(buildUri.toString());

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    movieJsonString = null;
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    movieJsonString = null;
                    return null;
                }
                movieJsonString = stringBuffer.toString();
                Log.v(LOG_TAG, "Movie string: " + movieJsonString);

            } catch (Exception e) {
                movieJsonString = null;
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
                getMovieInfoFromJson(movieJsonString);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            View pb = findViewById(R.id.loading_indicator);
            pb.setVisibility(View.GONE);
            if(movieAdapter!=null){
                recyclerview.setAdapter(movieAdapter);
            }
            movieAdapter.notifyDataSetChanged();
            task = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        Log.i(TAG, "onCreateOptionsMenu: ");

        MenuItem item = menu.findItem(R.id.menu_spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movieTypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spinner.getSelectedItem().toString().toLowerCase();

                switch (i){
                    case 0:
                        Log.i(TAG, "onItemSelected: "+"POPULAR");
                        if(task == null){
                            movieAdapter.clear();
                            selectedType = selectedItem;
                            task = new FetchMovieInfo();
                            task.execute(selectedItem,page+"");
                            break;

                        }

                    case 1:
                        if(task == null){
                            movieAdapter.clear();
                            Log.i(TAG, "onItemSelected: "+"TOP");
                            selectedType = "top_rated";
                            new FetchMovieInfo().execute("top_rated",page+"");
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

}
