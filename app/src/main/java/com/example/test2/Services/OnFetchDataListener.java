package com.example.test2.Services;

import com.example.test2.Model.AnimeData;

import java.util.List;

public interface OnFetchDataListener<JikanApiResponse> {

    void onFetchData(List<AnimeData> list, String message);
    void onError(String message);
}
