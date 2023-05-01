package com.example.test2.Services;

import com.example.test2.Model.GenreModel;

import java.util.List;

public interface OnFetchGenreListener<JikanGenreResponse> {

    void onFetchGenre(List<GenreModel> genreModelList, String message);
    void OnGenreError(String msg);
}
