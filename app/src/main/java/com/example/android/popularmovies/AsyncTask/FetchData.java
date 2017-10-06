package com.example.android.popularmovies.AsyncTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.Activities.DetailActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by anuj on 1/9/17.
 */

public class FetchData extends AsyncTask<String, Void, String[]> {

    private String BASE_URL = "https://api.themoviedb.org/3/movie";


    private String[] getVideoInfoFromJson(String jsonString, String type) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonString);
        JSONArray moviesArray = moviesJson.getJSONArray("results");

        String[] resultsStr = new String[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {
            String key;

            JSONObject movie = moviesArray.getJSONObject(i);

            if(type == "reviews")
                key = movie.getString("content");
            else
                key = movie.getString("key");


            Log.i(TAG, "getVideoInfoFromJson: RESULT["+i + "]= " + key );

            resultsStr[i] = key;

        }

        return resultsStr;
    }



    @Override
    protected String[] doInBackground(String... params) {

        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;



        String movieVideoString = null;

        try {
            Uri buildUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendPath(params[0])
                    .appendPath(params[1])
                    .appendQueryParameter("api_key", Utilities.getAppContext().getString(R.string.api_key))
                    .build();

            URL url = new URL(buildUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.i(TAG, "doInBackground: BUILDURI" +buildUri.toString());
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
            Log.i(TAG, "CATCH BLOCK: ");
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
            String[] s = getVideoInfoFromJson(movieVideoString, params[1]);
            return s;
        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
