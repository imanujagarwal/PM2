package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Activities.WebViewActivity;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anuj on 4/10/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {

    private ArrayList reviews;
    private Context context;

    public ReviewAdapter(ArrayList reviews, Context context){
        this.reviews = reviews;
        this.context = context;
    }

    public class VH extends RecyclerView.ViewHolder{

        TextView textView;

        public VH(View itemview){
            super(itemview);
            textView = (TextView) itemview.findViewById(R.id.recyclerview_review_item_tv);

        }

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_reviews,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {

        holder.textView.setText(reviews.get(position).toString());
    }


    @Override
    public int getItemCount() {
        return reviews.size();
    }


}
