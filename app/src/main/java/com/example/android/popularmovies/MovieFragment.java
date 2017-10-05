package com.example.android.popularmovies;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.Data.MovieContract;

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

import static android.content.ContentValues.TAG;
import static com.example.android.popularmovies.R.id.menu_spinner;
import static com.example.android.popularmovies.R.id.rv_movies;

/**
 * Created by anuj on 28/8/17.
 */

public class MovieFragment extends Fragment {

    MovieAdapter movieAdapter;
    List<GridItem> gridItemList;
    RecyclerView recyclerview;
    int selectedIndex =  -1;
    String selectedType = "";
    public FetchMovieInfo task = null;
    private EndlessScrollListener scrollListener;
    View view;
    GridLayoutManager gridLayoutManager;
    Spinner spinner;
    int spinnerState;
    private static final String RECYCLER_VIEW_POSITION_KEY = "rv_position_key";
    Parcelable listState;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_main,container,false);
        setHasOptionsMenu(true);
//        setRetainInstance(true);

        if(savedInstanceState!=null)
            listState=savedInstanceState.getParcelable(RECYCLER_VIEW_POSITION_KEY);


        recyclerview = (RecyclerView) view.findViewById(rv_movies);
        gridItemList = new ArrayList<GridItem>();


        movieAdapter = new MovieAdapter(getActivity(), gridItemList);

        if(movieAdapter!=null){
            recyclerview.setAdapter(movieAdapter);
        }

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerview.setLayoutManager(gridLayoutManager);
        }
        else{
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            recyclerview.setLayoutManager(gridLayoutManager);
        }

        if(savedInstanceState!=null) {
            spinnerState = savedInstanceState.getInt("yourSpinner");

        }


        scrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "onLoadMore - PAGE NO: "+page);
                if(selectedType == "fav")
                    return;
                loadnextdatafromApi(page);
            }
        };

        recyclerview.addOnScrollListener(scrollListener);



        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        saveRecyclerViewState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(selectedType == "fav"){
            movieAdapter.clear();
            LoadFavorites();
        }


    }

    private void restoreRecyclerViewState() {
        Parcelable parcelable = getArguments().getParcelable(RECYCLER_VIEW_POSITION_KEY);
        if (recyclerview.getLayoutManager() != null) {
            recyclerview.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }

    private void saveRecyclerViewState() {
        if (recyclerview.getLayoutManager() != null) {
            getArguments().putParcelable(
                    RECYCLER_VIEW_POSITION_KEY, recyclerview.getLayoutManager().onSaveInstanceState()
            );
        }
    }



    public void loadnextdatafromApi(int page){
        if(task == null && page < 25) {
            task = new FetchMovieInfo();
            task.execute(selectedType, page + "");

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("yourSpinner", spinner.getSelectedItemPosition());
        outState.putParcelable(RECYCLER_VIEW_POSITION_KEY, recyclerview.getLayoutManager().onSaveInstanceState());

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
//          Log.i(TAG, "onCreateOptionsMenu: ");

            MenuItem item = menu.findItem(R.id.menu_spinner);
            spinner = (Spinner) MenuItemCompat.getActionView(item);




            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.movieTypes_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
            spinner.setSelection(spinnerState);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    selectedItem = spinner.getSelectedItem().toString().toLowerCase().replaceAll("\\s","");

                    switch (i){
                        case 0:
                            if(task == null){
                                movieAdapter.clear();
                                task = new FetchMovieInfo();
                                selectedIndex = 0; selectedType = "popular";
                                task.execute("popular","1");
                                break;

                            }

                        case 1:
                            if(task == null){
                                movieAdapter.clear();
                                task = new FetchMovieInfo();
                                selectedIndex = 1; selectedType = "top_rated";
                                task.execute("top_rated","1");
                                break;
                            }

                        case 2:{

                            movieAdapter.clear();
                            selectedType = "fav";
                            LoadFavorites();
                            movieAdapter.notifyDataSetChanged();
                            break;

                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }



    public void LoadFavorites(){
        String[] projection = {MovieContract.FavoriteMovies.COLUMN_MOVIE_ID,
                MovieContract.FavoriteMovies.COLUMN_BACKDROP_PATH,
                MovieContract.FavoriteMovies.COLUMN_POSTER_PATH,
                MovieContract.FavoriteMovies.COLUMN_RELEASE_DATE,
                MovieContract.FavoriteMovies.COLUMN_OVERVIEW,
                MovieContract.FavoriteMovies.COLUMN_TITLE,
                MovieContract.FavoriteMovies.COLUMN_VOTE_AVERAGE
        };
        Cursor cursor = getContext().getContentResolver().query(MovieContract.FavoriteMovies.CONTENT_URI,
                projection,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        if(cursor.getCount() == 0)
            return;

        do{
            String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_POSTER_PATH));
            String backdropPath = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_BACKDROP_PATH));
            String name = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_TITLE));
            String synopsis = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_OVERVIEW));
            Double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_VOTE_AVERAGE));
            String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_RELEASE_DATE));
            String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_MOVIE_ID));
            gridItemList.add(new GridItem(backdropPath,posterPath,name,synopsis,rating,releaseDate,movieId));
        }while (cursor.moveToNext());
        movieAdapter.notifyDataSetChanged();
        view.findViewById(R.id.loading_indicator).setVisibility(View.GONE);

    }



    public class FetchMovieInfo extends AsyncTask<String, Void, String[]> {


        private String BASE_URL = "https://api.themoviedb.org/3/movie";

        private String KEY = "api_key"; //Do not change this value, key must be defined on strings.xml
        private String RESULTS = "results";
        private String MOVIE_NAME = "title";
        private String MOVIE_IMAGE = "poster_path";
        private String MOVIE_SYNOPSIS = "overview";
        private String MOVIE_RATING = "vote_average";
        private String MOVIE_RELEASE_DATE = "release_date";
        private String PAGE = "page";
        private String MOVIE_ID = "id";


        private String[] getMovieInfoFromJson(String movieJsonString) throws JSONException {

            JSONObject moviesJson = new JSONObject(movieJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            String[] resultsStr = new String[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                String name;
                String posterPath;
                String backdropPath;
                String synopsis;
                double rating;
                String released;
                String movieId;

                JSONObject movie = moviesArray.getJSONObject(i);
                name = movie.getString(MOVIE_NAME);
                posterPath = movie.getString(MOVIE_IMAGE);
                if(posterPath == "null" || posterPath.isEmpty()){
                    i--;
                    continue;
                }
                backdropPath = movie.getString(MovieContract.FavoriteMovies.COLUMN_BACKDROP_PATH);
                synopsis = movie.getString(MOVIE_SYNOPSIS);
                rating = movie.getDouble(MOVIE_RATING);
                released = movie.getString(MOVIE_RELEASE_DATE);
                movieId = movie.getString(MOVIE_ID);

                gridItemList.add(new GridItem(backdropPath,posterPath, name, synopsis, rating, released, movieId));

            }

            return resultsStr;
        }



        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String[] sArray = null;

            String movieJsonString = null;

            try {
                Uri buildUri = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(KEY, getActivity().getString(R.string.api_key))
                        .appendQueryParameter(PAGE, params[1])
                        .build();

                String test = buildUri.toString();
                Log.i(TAG, "URI: " +test);

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
                Log.v("LOG_TAG", "Movie string: " + movieJsonString);

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
                sArray = getMovieInfoFromJson(movieJsonString);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return sArray;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            View pb = view.findViewById(R.id.loading_indicator);
            pb.setVisibility(View.GONE);
            movieAdapter.notifyDataSetChanged();
            recyclerview.getLayoutManager().onRestoreInstanceState(listState);
            task = null;
        }
    }
    }
