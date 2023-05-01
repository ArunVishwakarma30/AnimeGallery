package com.example.test2.Model;

import java.util.Date;

public class FirebaseDataModel {
    private String detail, img_url, name;
    private  int eps, mal_id;
    private boolean userFav;
    Date dateTime;

    public FirebaseDataModel() {
    }

    public FirebaseDataModel(String detail, String img_url, String name, int eps, int mal_id, boolean userFav, Date dateTime) {
        this.detail = detail;
        this.img_url = img_url;
        this.name = name;
        this.eps = eps;
        this.mal_id = mal_id;
        this.userFav = userFav;
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEps() {
        return eps;
    }

    public void setEps(int eps) {
        this.eps = eps;
    }

    public int getMal_id() {
        return mal_id;
    }

    public void setMal_id(int mal_id) {
        this.mal_id = mal_id;
    }

    public boolean isUserFav() {
        return userFav;
    }

    public void setUserFav(boolean userFav) {
        this.userFav = userFav;
    }
}
