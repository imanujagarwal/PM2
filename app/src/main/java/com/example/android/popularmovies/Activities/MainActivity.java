package com.example.android.popularmovies.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.popularmovies.MovieFragment;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utilities;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Utilities.setContext(this);

        if(!Utilities.haveNetworkConnection()){
            Toast.makeText(this,"NO INTERNET CONNECTION \nPlease connect to Internet to properly load the data",Toast.LENGTH_LONG).show();
        }


        if(getSupportFragmentManager().findFragmentById(R.id.frag_container) == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,new MovieFragment(), "Movie_Fragment").commit();
        }
    }
}
