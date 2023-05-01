package com.example.test2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test2.Model.AnimeData;
import com.example.test2.Model.GenreModel;
import com.example.test2.R;
import com.example.test2.Services.SelectGenre;
import com.example.test2.Services.SelectListener;

import java.util.List;

public class AnimeGenreAdapter extends RecyclerView.Adapter<AnimeGenreAdapter.GenreViewHolder> {

    private Context context;
    private List<GenreModel> genreData;
    private SelectGenre listener;

    public AnimeGenreAdapter(Context context, List<GenreModel> genreData, SelectGenre listener) {
        this.context = context;
        this.genreData = genreData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenreViewHolder(LayoutInflater.from(context).inflate(R.layout.genre_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
            holder.animeGenre.setText(genreData.get(position).getName());

        Log.d("AllGEN", ""+genreData.get(position).getName() + " "+ genreData.get(position).getMal_id());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGenreClicked(genreData.get(position));
                }
            });
    }

    @Override
    public int getItemCount() {
        return genreData.size();
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder{

        TextView animeGenre;
        CardView cardView;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            animeGenre = itemView.findViewById(R.id.animeGenreTextView);
            cardView = itemView.findViewById(R.id.animeGenreContainer);

        }
    }
}
