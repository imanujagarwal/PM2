package com.example.android.popularmovies.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.MovieFragment;
import com.example.android.popularmovies.R;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(getSupportFragmentManager().findFragmentById(R.id.frag_container) == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,new MovieFragment(), "Movie_Fragment").commit();
        }
    }
}
