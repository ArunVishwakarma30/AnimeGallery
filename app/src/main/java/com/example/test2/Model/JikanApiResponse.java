package com.example.test2.Model;

import java.io.Serializable;
import java.util.List;

public class JikanApiResponse implements Serializable {

    List<AnimeData> data;

    public List<AnimeData> getData() {
        return data;
    }

    public void setData(List<AnimeData> data) {
        this.data = data;
    }
}
