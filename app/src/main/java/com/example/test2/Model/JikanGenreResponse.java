package com.example.test2.Model;

import java.io.Serializable;
import java.util.List;

public class JikanGenreResponse implements Serializable {

    List<GenreModel> data ;

    public List<GenreModel> getData() {
        return data;
    }

    public void setData(List<GenreModel> data) {
        this.data = data;
    }
}
