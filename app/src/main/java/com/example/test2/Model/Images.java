package com.example.test2.Model;

import java.io.Serializable;

public class Images implements Serializable {
    Jpg jpg = null;
    Webp webp = null;

    public Webp getWebp() {
        return webp;
    }

    public void setWebp(Webp webp) {
        this.webp = webp;
    }

    public Jpg getJpg() {
        return jpg;
    }

    public void setJpg(Jpg jpg) {
        this.jpg = jpg;
    }
}
