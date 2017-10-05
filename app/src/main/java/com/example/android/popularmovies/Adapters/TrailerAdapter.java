package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Activities.WebViewActivity;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anuj on 4/10/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.VH> {

    private ArrayList picsToBeLoaded;
    private Context context;

    public TrailerAdapter(ArrayList picsToBeLoaded, Context context){
        this.picsToBeLoaded = picsToBeLoaded;
        this.context = context;
    }

    public class VH extends RecyclerView.ViewHolder{

        ImageView img;

        public VH(View itemview){
            super(itemview);
            img = (ImageView)itemview.findViewById(R.id.img_trailer);

        }

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_trailer,parent,false);
        if(picsToBeLoaded.size() < 1)
            v.findViewById(R.id.trailer_recyclerview).setVisibility(View.GONE);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        Picasso.with(context).load(getTrailerImage(picsToBeLoaded.get(position).toString(),"hqdefault")).into(holder.img);



        if(isPackageInstalled("com.google.android.youtube")){
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://m.youtube.com/watch?v=" + picsToBeLoaded.get(position)));
                    intent.setPackage("com.google.android.youtube");
                    context.startActivity(intent);
                }
            });
        }else {
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,WebViewActivity.class);
                    intent.putExtra("youtubeUrl","https://m.youtube.com/watch?v=" + picsToBeLoaded.get(position));
                    context.startActivity(intent);
                }
            });

        }
    }

    private boolean isPackageInstalled(String packagename) {
        try {
//            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            context.getPackageManager().getPackageInfo(packagename,0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getTrailerImage(String youtubeId, String image_number){
        return "https://img.youtube.com/vi/" + youtubeId + "/" + image_number +".jpg" ;
    }

    @Override
    public int getItemCount() {
        return picsToBeLoaded.size();
    }


}
