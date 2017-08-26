package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;
import static android.media.CamcorderProfile.get;

/**
 * Created by anuj on 4/3/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.Viewholder>{

    private Context context;
    public List<GridItem> mGridItemList;
    public final String BASE_URL = "http://image.tmdb.org/t/p/w500//";


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

    public MovieAdapter(Context context, List<GridItem> mGridItemList){
        this.context = context;
        this.mGridItemList = mGridItemList;

    }



    @Override
    public int getItemCount() {
        return mGridItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void clear() {
        int size = this.mGridItemList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mGridItemList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView moviePoster;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public Viewholder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.imageView_movieArtwork);

        }



    }

    @Override
    public MovieAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item,parent,false);
        Viewholder viewholder = new Viewholder(itemview);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        GridItem gridItem = mGridItemList.get(position);

        String Url = BuildImageUrl(gridItem.getmImageUrlSuffix());

        Picasso.with(context)
                .load(Url)
                .into(holder.moviePoster);

        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra(MOVIE_NAME,mGridItemList.get(position).getmTitle());
                intent.putExtra(MOVIE_IMAGE,mGridItemList.get(position).getmImageUrlSuffix());
                intent.putExtra(MOVIE_SYNOPSIS,mGridItemList.get(position).getmOverview());
                intent.putExtra(MOVIE_RATING, mGridItemList.get(position).getmRating());
                intent.putExtra(MOVIE_RELEASE_DATE,mGridItemList.get(position).getmReleaseDate());
                intent.putExtra(MOVIE_ID,mGridItemList.get(position).getmMovieId());
                Log.i(TAG, "onItemClick: VALUES "+mGridItemList.get(position).getmTitle());
                context.startActivity(intent);
            }
        });


    }


    public String BuildImageUrl(String ImageSuffix){
        return BASE_URL+ImageSuffix;

    }
}
