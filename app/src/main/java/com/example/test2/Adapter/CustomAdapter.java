package com.example.test2.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test2.Model.AnimeData;
import com.example.test2.Model.Genre;
import com.example.test2.R;
import com.example.test2.Services.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewholder> {

    private Context context;
    private List<AnimeData> headlines;
    private SelectListener listener;

    public CustomAdapter(Context context, List<AnimeData> headlines, SelectListener listener) {
        this.context = context;
        this.headlines = headlines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewholder(LayoutInflater.from(context).inflate(R.layout.headline_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewholder holder, int position) {

        final AnimeData currentData = headlines.get(position);

        if (currentData.getTitle_english() == null) {
            holder.text_title.setText(currentData.getTitle());
        } else {
            holder.text_title.setText(currentData.getTitle_english());
        }

        String episodes = "";
        if (currentData.getEpisodes() <= 1) {

            episodes = "";
        } else {
            episodes = "Episodes : " + String.valueOf(currentData.getEpisodes());
        }

        holder.text_eps.setText(episodes);

        Log.d("img", "" + currentData.getImages().getJpg().getImage_url());
        List<Genre> temp = currentData.getGenres();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            temp.stream().forEach((crunchifyTemp) -> Log.d("AnimeGen", "" + crunchifyTemp.getName()));
        }

        // Log.d("fav", " " + currentData.isFavourites());

        // Setting image
        if (currentData.getImages().getJpg().getImage_url() != null) {
            Picasso.get().load(currentData.getImages().getJpg().getImage_url()).into(holder.img_headlines);
        }

        holder.img_headlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnimeClicked(headlines.get(position));
            }
        });

        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // popUp menu click
        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDotsClicked(headlines.get(position), holder.cardView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }
}
