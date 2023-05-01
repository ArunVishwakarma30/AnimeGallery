package com.example.test2.Services;

import androidx.cardview.widget.CardView;

import com.example.test2.Model.AnimeData;

public interface SelectListener {
    public void onAnimeClicked(AnimeData animeData);


    public void onDotsClicked(AnimeData animeData, CardView cardView);
}
