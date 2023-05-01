package com.example.test2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test2.Model.AnimeData;
import com.example.test2.Model.Genre;
import com.example.test2.R;
import com.example.test2.Services.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllAnimeAdapter extends RecyclerView.Adapter<AllAnimeAdapter.Viewholder> {

    private Context context;
    private List<AnimeData> animeData;
    private SelectListener listener;

    public AllAnimeAdapter(Context context, List<AnimeData> animeData, SelectListener listener) {
        this.context = context;
        this.animeData = animeData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.all_anime_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") int position) {
        final AnimeData currentData = animeData.get(position);

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



        // Setting image
        if (currentData.getImages().getJpg().getImage_url() != null) {
            Picasso.get().load(currentData.getImages().getJpg().getImage_url()).into(holder.img_headlines);
        }

        holder.img_headlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnimeClicked(animeData.get(position));
            }
        });

        // popUp menu click
        holder.allAnimeThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDotsClicked(animeData.get(position), holder.allAnimeContainer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return animeData.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView text_title, text_eps;
        ImageView img_headlines, allAnimeThreeDots;
        CardView allAnimeContainer;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            text_title = itemView.findViewById(R.id.allAnimeTitle);
            img_headlines = itemView.findViewById(R.id.allAnimeImg);
            text_eps = itemView.findViewById(R.id.AllAnimeEps);
            allAnimeThreeDots = itemView.findViewById(R.id.AllAnimeThreeDots);
            allAnimeContainer = itemView.findViewById(R.id.allAnimeContainer);
        }
    }

}
